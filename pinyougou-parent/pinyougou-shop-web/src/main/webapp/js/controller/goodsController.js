 //控制层 
app.controller('goodsController' ,function($scope,$controller,typeTemplateService,itemCatService,goodsService,uploadService){	
	
	$controller('baseController',{$scope:$scope});//继承
	$scope.entity={
			itemList:[],
			goodsDesc:{
					itemImages:[],
					specificationItems:[]
					}
	};//定义实体
	//创建SKU列表
	$scope.createItemList=function(){	
		$scope.entity.itemList=[{spec:{},price:0,num:99999,status:'0',isDefault:'0' } ];//初始
		var items=  $scope.entity.goodsDesc.specificationItems;	
		for(var i=0;i< items.length;i++){
			$scope.entity.itemList = addColumn( $scope.entity.itemList,items[i].attributeName,items[i].attributeValue );    
		}	
	}
	//添加列值 
	addColumn=function(list,columnName,conlumnValues){
		var newList=[];//新的集合
		for(var i=0;i<list.length;i++){
			var oldRow= list[i];
			for(var j=0;j<conlumnValues.length;j++){
				var newRow= JSON.parse( JSON.stringify( oldRow )  );//深克隆
				newRow.spec[columnName]=conlumnValues[j];
				newList.push(newRow);
			}    		 
		} 		
		return newList;
	}
	

	//根据指定属性查询指定对象
	$scope.searchListByAttribute=function(list,key,value){
		for(var i=0;i<list.length;i++){
			if(list[i][key]==value){
				return list[i];
			}			
		}		
		return null;
	}
	
	$scope.isChecked=function($event,name,value){
		var obj = $scope.searchListByAttribute($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		if(obj!=null){
			if($event.target.checked==true){
				obj.attributeValue.push(value)//如果选中了就把指定值存起来
			}else{
				obj.attributeValue.splice(object.attributeValue.indexOf(value) ,1);
				//如果选项都取消了，将此条记录移除
				if(object.attributeValue.length==0){
					$scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}	
			}
		}else{
			$scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}
		$scope.createItemList();
	}
	
	
	
	
	
	$scope.selectItemCatList=function(){
		itemCatService.findByParentId(0).success(function(response){
				$scope.itemCat1List=response;
		})
	}
	$scope.$watch('entity.goods.category1Id', function(newValue, oldValue) {  
    	//根据选择的值，查询二级分类
    	itemCatService.findByParentId(newValue).success(
    		function(response){
    			$scope.itemCat2List=response; 	    			
    		}
    	);    	
	}); 
	$scope.$watch('entity.goods.category2Id', function(newValue, oldValue) {          
    	//根据选择的值，查询三级分类
    	itemCatService.findByParentId(newValue).success(
    		function(response){
    			$scope.itemCat3List=response; 	    			
    		}
    	);    	
	}); 
	$scope.$watch('entity.goods.category3Id', function(newValue, oldValue) {          
    	//根据选择的值，得到模板id
    	itemCatService.findOne(newValue).success(  //根据三级类型的id查询typeId
    		function(response){
    			 $scope.entity.goods.typeTemplateId=response.typeId;  //得到类型id	    			
    		}
    	);    	
	}); 
	$scope.$watch('entity.goods.typeTemplateId', function(newValue, oldValue) {     
    	//根据选择的值，得到模板id
    	typeTemplateService.findOne(newValue).success(  //根据模板类型id查询模板类型对象
    		function(response){
    			 $scope.typeTemplate=response;  //模板对象赋值	  
    			 $scope.typeTemplate.brandIds=JSON.parse(response.brandIds);//列表是对象 不能是字符串
    			 $scope.entity.goodsDesc.customAttributeItems= JSON.parse(response.customAttributeItems)//是扩展属性列表
    			 $scope.specList= JSON.parse(response.specIds)//是扩展属性列表
    		}
    	);    	
    	typeTemplateService.findSpecList(newValue).success(function(response){
    		 $scope.specList=response;
    	})
    	
    	
	}); 
	
	
	
    //添加图片列表
    $scope.add_image_entity=function(){    	
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }
    
    $scope.delImage=function(index){
    	$scope.entity.goodsDesc.itemImages.splice(index,1)
    }
    
	
	//文件上传功能
	$scope.uploadFile=function(){
		uploadService.uploadFile().success(function(response){
			if(response.success){//如果上传成功，取出url
        		$scope.image_entity.url=response.message;//设置文件地址
        	}else{
        		alert(response.message);
        	}
		})
	}
	
	//用来做添加功能的
	$scope.add=function(){
		$scope.entity.goodsDesc.introduction = editor.html(); //把富文本编辑器的内容放到对象中
		goodsService.add($scope.entity).success(function(response){
			if(response.success){
				alert('保存成功');					
				$scope.entity={};
				editor.html('');//清空富文本编辑器
			}else{
				alert(response.message);
			}
		})
	}
	
	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
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
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
