 //控制层 
app.controller('contentController' ,function($scope,contentService){	
	
	$scope.contentList=[];
	
	$scope.findByCategoryId=function(categoryid){
		
		contentService.findByCategoryId(categoryid).success(function(response){
			$scope.contentList[categoryid]=response; //根据分类id进行存放广告 ，因为要在不同的分类情况下展示不同的广告轮播
		})
	}
	
	$scope.search=function(){
		window.location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
	}
	
});	
