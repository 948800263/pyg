//服务层
app.service('contentService',function($http){
	
	this.findByCategoryId=function(id){
		return $http.post("../content/findByCategoryId.do?categoryId="+id)
	}
	    	 	
});
