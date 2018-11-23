//购物车服务层
app.service('cartService',function($http){
	//购物车列表
	this.findCartList=function(){
		return $http.get('cart/findCartList.do');		
	}
	//购物车列表
	this.addGoodsToCartList=function(itemId,num){
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+"&num="+num);		
	}
	//统计
	this.sum=function(cartList){
		var totalValue={totalNum:0, totalMoney:0.00 };//合计实体
		
		for(var i=0;i<cartList.length;i++){
			
			for(var j=0;j<cartList[i].orderItemList.length;j++){
				
				totalValue.totalNum+=cartList[i].orderItemList[j].num;
				totalValue.totalMoney+=cartList[i].orderItemList[j].totalFee;
				
			}
		}
		return totalValue;
		
	}
	
});