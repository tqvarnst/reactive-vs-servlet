'use strict';

angular.module("app")

.factory('catalog', ['$http', '$q', 'COOLSTORE_CONFIG', 'Auth', '$location', function($http, $q, COOLSTORE_CONFIG, $auth, $location) {
	var factory = {}, products, baseUrl;

	if ($location.protocol() === 'https') {
		baseUrl = (COOLSTORE_CONFIG.SECURE_API_ENDPOINT.startsWith("https://") ? COOLSTORE_CONFIG.SECURE_API_ENDPOINT : "https://" + COOLSTORE_CONFIG.SECURE_API_ENDPOINT + '.' + $location.host().replace(/^.*?\.(.*)/g,"$1")) + '/services/products';
	} else {
		baseUrl = (COOLSTORE_CONFIG.API_ENDPOINT.startsWith("http://") ? COOLSTORE_CONFIG.API_ENDPOINT : "http://" + COOLSTORE_CONFIG.API_ENDPOINT + '.' + $location.host().replace(/^.*?\.(.*)/g,"$1")) + '/services/products';
	}

    factory.getProducts = function() {
		var deferred = $q.defer();
        if (products) {
            deferred.resolve(products);
        } else {
            $http({
                method: 'GET',
								url: baseUrl
            }).then(function(resp) {
                products = resp.data;
                deferred.resolve(resp.data);
            }, function(err) {
                deferred.reject(err);
            });
        }
	   return deferred.promise;
	};

	return factory;
}]);

angular.module("app").all("/services/*", function (req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Cache-Control, Pragma, Origin, Authorization, Content-Type, X-Requested-With");
    res.header("Access-Control-Allow-Methods", "GET, PUT, POST");
    return next();
});
