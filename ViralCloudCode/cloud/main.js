
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
    var addedFriend    = false;
    var addedUser      = false;
    var deletedRequest = false;
    var failed         = false;

    // Parse Classes
    var Request = Parse.Object.extend("Request");
    var Contact = Parse.Object.extend("Contact");
    var Post    = Parse.Object.extend("Post");

	  var user      = Parse.User.current();
	  var requestId = request.params.requestId;

    var query = new Parse.Query(Request);
    query.get(requestId, {
       success: function(request) {
           response.success("Request Id: " + request.id + ". Request From: " + request.get("nameFrom"));
       },
       error: function(request, error) {
           //if(!failed) response.error("Failed on getting the request: " + error.message + " Request ID: " + requestId);
           throw new Error("Failed on getting the request: " + error.message + " Request ID: " + requestId);
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


