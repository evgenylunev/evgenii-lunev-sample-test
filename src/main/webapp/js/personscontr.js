var personsapp = angular.module('personsapp', ["xeditable"]);

personsapp.controller('PersonsCtrl', function($scope, $http) {
	
	$scope.editable = false;
	
	 $scope.checkName = function(data, id) {
		  
		 	console.log("checkName person id:" + id);
		 	console.log("checkName desc data:" + data);
		    
	 };
	 
	 $scope.saveUser = function(data, id) {
		   
		 console.log(" saveUser person id:" + data.description);
		 console.log("saveUser data name:" + data.name);
		 var personObj = {
					id: id,
					name : data.name,
					description : data.description
			};
		 var req = {
			      method: 'POST',
			      url: '/epam/rest/datastore/update/',
			      data: personObj 
			    };
			    $http(req)				
			    .success(function(data) {
			    	$scope.listPersons();
			    });
	  }; 
	
	  
	  $scope.addPersons = function() {
		 
		  var dataObj = {
					name : $scope.personName,
					description : $scope.personDescription
			};
		   $http.put('/epam/rest/datastore/add/', dataObj)
	      .success(function (data) {
	         $scope.listPersons();
	      })
	      .error(function (data, status, headers, config) {
	          $scope.errorMessage = "Couldn't load the list of weather, error # " + status;
	      });  
	    
	   
	  };

	  $scope.listPersons = function() {
		
	    var req = {
	      method: 'GET',
	      url: '/epam/rest/datastore/list/'
	    };
	    $http(req).success(function(data) {
	      $scope.personlist = data;
	    });
	  };
	  
	  $scope.deletePerson = function(person) {
			console.log("person id:" + person.id);
			
			var headers = {
					'Content-Type': 'application/json'	
			};
		    var req = {
		      method: 'DELETE',
		      url: '/epam/rest/datastore/delete/',
		      data: person, 
		      headers: headers
		    };
		    $http(req)
		    .success(function(data) {
		    	$scope.listPersons();
		    });
		  };

	  
	  $scope.listPersons();
	});