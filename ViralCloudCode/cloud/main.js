
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
	response.success("Hello world!");
});

Parse.Cloud.define("sendRequest", function(request, response) {
	var nameFrom = Parse.User.current().get("username");
	var idFrom = Parse.User.current().id;
	var to   = request.params.to;
	
	var Request = Parse.Object.extend("Request");
	var request = new Request();
	request.set("idFrom", idFrom);
	request.set("nameFrom", nameFrom);
	request.set("to", to);
	request.save(null, {
		success: function(request) {
			response.success("successfully saved new request: " + request.id);
		},
		error: function(error) {
			response.error("Failed on saving new request: " + error.message);
		}
	});
});

/*
 * Adds the user and the person who sent the request to each other's contacts list
 * and the deletes the request
 */
Parse.Cloud.define("acceptRequest", function(request, response) {
    Parse.Cloud.useMasterKey();

    // Task checks
    var addedFriend = false;
    var addedUser = false;
    var deletedRequest = true;

	  var user = Parse.User.current();
    var senderId;
	  var requestId = request.params.requestId;

    var Request = Parse.Object.extend("Request");
    var query = new Parse.Query(Request);
    query.get(requestId, {
       success: function(request) {
           senderId = request.get("idFrom");
           console.log(senderId);

           // Retrieve the friend's data
           //var User = Parse.Object.extend("User");
           var query = new Parse.Query(Parse.User);
           query.get(senderId, {
               success: function(friend) {

                   var Contact = Parse.Object.extend("Contact");

                   // -----CONVERT FRIEND TO CONTACT-----
                   var contact1 = new Contact();
                   contact1.set("id", friend.id);
                   contact1.set("name", friend.get("username"));

                   contact1.save(null, {
                       success: function(savedContact) {

                           // Add the friend to the user's contacts
                           var relation = user.relation("contacts");
                           relation.add(savedContact);
                           user.save(null, {
                              success: function(savedUser) {
                                  addedFriend = true;
                                  if(addedUser & deletedRequest) response.success("Finished accepting request");
                              },
                              error: function(error) {
                                  console.log("Failed on adding the friend to user's contacts: " + error.message);
                                  response.error("Failed on adding the friend to user's contacts: " + error.message);
                                  return;
                              }
                    	   });
                       },
                       error: function(error) {
                           console.log("Failed on making the friend a contact: " + error.message);
                           response.error("Failed on making the friend a contact: " + error.message);
                           return;
                       }
                   });

                   // -----CONVERT USER TO CONTACT-----
                   var contact2 = new Contact();
                   contact2.set("id", user.id);
                   contact2.set("name", user.get("username"));

                   contact2.save(null, {
                       success: function(savedContact) {

                           // Add the user to the friend's contacts
                           var relation = friend.relation("contacts");
                           relation.add(savedContact);
                           friend.save(null, {
                               success: function(savedFriend) {
                                   addedUser = true;
                                   if(addedFriend & deletedRequest) response.success("Finished accepting request");
                               },
                               error: function(error) {
                                   console.log("Failed on adding the user to the friend's contacts: " + error.message);
                                   response.error("Failed on adding the user to the friend's contacts: " + error.message);
                                   return;
                               }
                           });
                       },
                       error: function(error) {
                           console.log("Failed on making user a contact: " + error.message + "; User id: " + friend.id + ". Friend name: " + friend.get("username"));
                           response.error("Failed on making user a contact: " + error.message + "; Friend id: " + friend.id + ". Friend name: " + friend.get("username"));
                           return;
                       }
                   });
               },
               error: function(object, error) {
                   console.log("Failed on retrieving friend's data: " + error.message);
                   response.error("Failed on retrieving friend's data: " + error.message);
                   return;
               }
           });

           /* -----DELETE THE REQUEST-----
           request.destroy({
              success: function(request) {
                  deletedRequest = true;
                  if(addedFriend & addedUser) response.success("Finished accepting request");
              },
              error: function(error) {
                  response.error("Failed on deleting the request: " + error.message);;
              }
           });*/
       },
       error: function(error) {
           response.error("Failed on Request Query: " + error.message);
           return;
       }
    });

});

/*
 * Simply deletes the specified request
 */
Parse.Cloud.define("declineRequest", function(request, response) {
    Parse.Cloud.useMasterKey();

    var requestId = request.params.requestId;

    var Request = Parse.Object.extend("Request");
    var query = new Parse.Query(Request);
    query.equalTo("objectId", requestId);
    query.find({
        success: function(requests) {
            requests[0].destroy({
               success: function(request) {
                   response.success("Successfully declined the request");
               },
               error: function(error) {
                   response.error("Failed on deleting the request: " + error.message);
               }
            });
        },
        error: function(error) {
            response.error("Failed on Request Query: " + error.message);
        }
    });
});

/*
 * Retrieves the user's recent items, like new posts, requests, and contacts 
 * and sends them back in an array
 */
Parse.Cloud.define("getItems", function(request, response) {
    Parse.Cloud.useMasterKey();
    var user = Parse.User.current();

	// Find the user's contacts
	var contactsRelation = user.relation("contacts");
	contactsRelation.query().find({
		success: function(contacts) {
		
			// Find all posts shared with the user
			var cNames = new Array();
			for (var i = 0; i < contacts.length; i++) {
				cNames[cNames.length] = contacts[0].get("name");;
			}
			
			var Post = Parse.Object.extend("Post");
	    	var postQuery = new Parse.Query(Post);
	   	 	postQuery.containedIn("name", cNames);
	    	postQuery.find({
	       		success: function(posts) {
           
	            	// Find any requests sent to the user
	            	var Request = Parse.Object.extend("Request");
	            	var requestQuery = new Parse.Query(Request);
	            	requestQuery.equalTo("to", user.get("username"));
	            	requestQuery.find({
	            		success: function(requests) {
	            			
	            			// Package the items together and send them back
	            			var items = new Array(contacts, posts, requests);
	            			response.success(items);
	            		},
	            		error: function(error) {
	            			response.error("Failed on finding request: " + error.message);
	            		}
	            	});
	       		},
	       		error: function(error) {
	           		response.error("Failed on finding posts: " + error.message);
	      		}
	    	});
		},
		error: function(error) {
			response.error("Failed on finding contacts: " + error.message);
		}
	});
	    
});


