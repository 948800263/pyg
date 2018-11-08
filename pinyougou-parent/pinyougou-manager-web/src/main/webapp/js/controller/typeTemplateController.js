 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller   ,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.brandList={data:[]};//品牌数据
	$scope.specList={data:[]};//品牌数据
	//转换json为文字
	$scope.jsonToString=function(t){
		
		var list1 = JSON.parse(t);
		var str = "";
		for(var i = 0;i<list1.length;i++){
			if(i<list1.length-1){
				str+=list1[i].text+","
			}else{
				str+=list1[i].text
			}
		}
		return str
	}
	
	
	//查询规格
	$scope.selectSpecList = function(){
		typeTemplateService.selectSpecList().success(
				function(response){
					$scope.specList={data:response};
				}
		)
	}
	
	//新增扩展属性行
	$scope.addTableRow=function(){	
		$scope.entity.customAttributeItems.push({});		
	}
	//删除扩展属性行
	$scope.deleTableRow=function(index){			
		$scope.entity.customAttributeItems.splice(index,1);//删除			
	} 
	
	//查询品牌
	$scope.selectOptionList = function(){
		typeTemplateService.selectOptionList().success(
				function(response){
					$scope.brandList={data:response};
				}
		)
	}
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				$scope.entity.specIds = JSON.parse(response.specIds)
				$scope.entity.brandIds= JSON.parse(response.brandIds)
				$scope.entity.customAttributeItems =JSON.parse(response.customAttributeItems)
				
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	