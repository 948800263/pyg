app.controller('searchController',function($scope,$location,searchService){	
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'totalPages':0,'total':0,'sortField':'','sort':'' };//搜索对象
	
	//加载查询字符串
	$scope.loadkeywords=function(){
	var keywords =	$location.search()['keywords'];
		
		if(keywords!=null&&keywords!=''){
			$scope.searchMap.keywords= keywords;
		}else{
			$scope.searchMap.keywords = "手机";
		}
		$scope.search();
	}
	
	
	//判断关键字是不是品牌  如果是 就隐藏品牌列表
	$scope.keywordsIsBrand=function(){
	
		
		
		var keywords = $scope.searchMap.keywords;
		
		for(var i = 0; i < $scope.resultMap.brandList.length;i++){
			if(keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true
			}
		}
		return false;
	}
	
	
	$scope.sortSearch=function(key,value){
		$scope.searchMap.sortField=key;
		$scope.searchMap.sort=value;
		$scope.search()
	}
	
	
	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.searchMap.totalPages){
			return true
		}else{
			return false
		}
	}
	
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true
		}else{
			return false
		}
	}
	
	//创建展示的所有的页码  就是5个页码
	$scope.buildPageLabel=function(){
		$scope.pageLabel=[];//新增分页栏属性	
		var startPage = 1;
		var endPage = 5;
		var pageNo = $scope.searchMap.pageNo;
		var totalPages = $scope.searchMap.totalPages;
		$scope.qd = false;
	    $scope.hd = false;
		if(pageNo<3){
			startPage=1;
			endPage=5;
			$scope.qd = false;
			if(totalPages>5){
				$scope.hd = true;
			}else{
				$scope.hd =false;
			}
		}else if(pageNo>totalPages-2){
			startPage=totalPages-4;
			endPage = totalPages;
			$scope.hd =false;
			if(totalPages-4<=1){
				$scope.qd=false;
			}else{
				$scope.qd=true;
			}
		}else{
			startPage=pageNo-2;
			endPage = pageNo+2;
			if(startPage<=1){
				$scope.qd=false
			}else{
				$scope.qd=true;
			}
			if(endPage<=5){
				$scope.hd =false
			}else{
				$scope.hd =true;
			}
			
		}
		for(var i = startPage;i<=endPage;i++){
			$scope.pageLabel.push(i);
		}
		
	}
	
	
	
	
	$scope.removeSearchItem=function(key){
		if(key=='keywords' || key=='category' || key=='brand' || key=='price' || key=='pageNo' || key=='pageSize' || key=='sortField' || key=='sort'){
			$scope.searchMap[key]="";
		}else{
			delete $scope.searchMap.spec[key];
		}
		$scope.search();
	}
	
	//添加赋值操作
	$scope.addSearchItem=function(key,value){
		
		if(key=='keywords' || key=='category' || key=='brand' || key=='price' || key=='pageNo' || key=='pageSize' || key=='sortField' || key=='sort'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		$scope.search();
	}
	
	
	//搜索
	$scope.search=function(){
		searchService.search( $scope.searchMap ).success(
			function(response){						
				$scope.resultMap=response;//搜索返回的结果
				$scope.searchMap.totalPages= response.totalPages;
				$scope.searchMap.total = response.total
				$scope.buildPageLabel()
			}
		);	
	}	
	
	//根据页码查询
	$scope.queryByPage=function(pageNo){
		//页码验证
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return;
		}
		$scope.searchMap.pageNo=pageNo;			
		$scope.search();
	}
	
	
});