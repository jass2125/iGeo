var app = angular.module("app");

app.config(function ($routeProvider) {
    $routeProvider.when("/login", {
        templateUrl: "login.html",
        controller: "loginController"
    }).when("/register", {
        templateUrl: "/register.html",
        controller: "registerController"
    }).when("/home", {
        templateUrl : "/home.html"
    }).otherwise({
        redirectTo: "/index.html"
    });
});
