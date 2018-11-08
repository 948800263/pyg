app.service("loginService",function($http){
	
	this.getName=function(){
		return $http.post("../login/getName.do")
	}
	
})