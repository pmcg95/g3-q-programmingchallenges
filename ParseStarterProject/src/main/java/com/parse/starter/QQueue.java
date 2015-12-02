package com.parse.starter;

import com.parse.*;
import java.util.List;
import android.util.Log;


/**
 * Created by DopeDev on 11/30/15.
 */
public class QQueue {

    public static void addSelfToQueue(final String company, final String myId, final SaveCallback callback) {

        getPlaceInQueue(company, myId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("QueuePlace");
                    query.whereEqualTo("company", company);
                    query.orderByDescending("place");

                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            int place = 0;
                            if (object == null) {
                                Log.d("place", "The getFirst request failed.");
                            } else {
                                place = object.getNumber("place").intValue() + 1;
                            }

                            ParseObject queuePlace = new ParseObject("QueuePlace");
                            queuePlace.put("userID", myId);
                            queuePlace.put("company", company);
                            queuePlace.put("place", place);
                            ParseACL acl = new ParseACL();
                            acl.setPublicReadAccess(true);
                            acl.setPublicWriteAccess(true);
                            queuePlace.setACL(acl);
                            queuePlace.saveInBackground(callback);
                        }
                    });
                } else {
                    Log.d("place", "Can't add self because I already exist");
                }

            }
        });


    }

    public static void getPlaceInQueue(final String company, String myId, final GetCallback<ParseObject> callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("QueuePlace");

        query.whereEqualTo("company", company);
        query.whereEqualTo("userID", myId);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(final ParseObject queuePlaceObject, ParseException e) {
                if (queuePlaceObject == null) {
                    callback.done(null, null);
                } else {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Company");

                    query.whereEqualTo("name", company);

                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (object == null) {
                                callback.done(null, null);
                            } else {
                                int queueStart = queuePlaceObject.getInt("place") - object.getInt("queueStart");
                                ParseObject returnObj = new ParseObject("QueuePlace");
                                returnObj.put("place", queueStart);
                                callback.done(returnObj, null);
                            }
                        }
                    });
                }
            }
        });
    }

    public static void removeSelfFromQueue(final String company, String myId, final DeleteCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("QueuePlace");

        query.whereEqualTo("company", company);
        query.whereEqualTo("userID", myId);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(final ParseObject queuePlaceObject, ParseException e) {
                if (queuePlaceObject == null) {
                    Log.d("Remove", "Already removed");
                } else {
                    int currPlace = queuePlaceObject.getInt("place");
                    Log.d("d", "curr " + currPlace);
                    queuePlaceObject.deleteInBackground(callback);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("QueuePlace");
                    query.whereGreaterThan("place", currPlace);
                    query.whereEqualTo("company", "Q");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objectList, ParseException e) {
                            if (e == null) {
                                for (int i = 0; i < objectList.size(); i++) {
                                    ParseObject object = objectList.get(i);
                                    object.put("place", object.getInt("place") - 1);
                                    object.saveInBackground();
                                }
                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    public static void getInfoAboutMe(String myId, final GetCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("QUser");

        query.whereEqualTo("userID", myId);

        query.getFirstInBackground(callback);
    }

}
