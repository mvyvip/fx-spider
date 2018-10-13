var lottery = {
	index: 1, //当前转动到哪个位置，起点位置
	count: 8, //总共有多少个位置
	timer: 'acc', //setTimeout的ID，用clearTimeout清除
	speed: 20, //初始转动速度
	times: 0, //转动次数
	cycle: 50, //转动基本次数：即至少需要转动多少次再进入抽奖环节
	prize: -1, //中奖位置
	shareCount:0,
	phone:'',
	areaId:'',
	countx:0,
	init: function(id) {
		if($("#" + id).find(".lottery-unit").length > 0) {
			$lottery = $("#" + id);
			$units = $lottery.find(".lottery-unit");
			this.obj = $lottery;
			this.count = $units.length;
			$lottery.find(".lottery-unit-" + this.index).addClass("active");
		};
	},
	roll: function() {
		var index = this.index;
		var count = this.count;
		var lottery = this.obj;
		$(lottery).find(".lottery-unit-" + index).removeClass("active");
		index += 1;
		if(index > count - 1) {
			index = 0;
		};
		$(lottery).find(".lottery-unit-" + index).addClass("active");
		this.index = index;

		return false;
	},
	stop: function(index) {

		this.prize = index;

		return false;
	},
	shareOK: function() {
		_this=this
		$.getJSON(href+'/wechat/addDrawCount?activityCode=2&areaId='+this.areaId+'&phone='+this.phone,function(data){
				_this.getCount()
		})
		$('#lottery').css('background','url(../img/one@3x.png)').css('animation','heart 0.5s  infinite;')
	},
	userInfo:function(a,b){
		_this=this
		_this.areaId=a
		_this.phone=b
		
		lottery.getCount()
		
	},
	share:function(){
		
		if(getparams('isWechat')){
			$('#pleaseShare').hide()
			$('#wechatshow').show()
		}else{
		window.location.href='feedel://activity/share?url='+window.location.href+'?isWechat=1&title=飞带社区抽奖专区&description=狗年开运，飞带送你欧洲双人游，快来试试您的运气吧~'
		}


		

	},
	submit:function(){
		if(!$('#selectArea').attr('sel')){
			$('body').toast({
						position:'fixed',
						animateIn:'fadeIn',
						animateOut:'fadeOut',
						content:'请选择区号',
						duration:2000,
						isCenter:true,
						});
						setTimeout(function(){
							$('.animated ').remove()
						},2000)
		}else if($('.phone').val().length==0){
			$('body').toast({
						position:'fixed',
						animateIn:'fadeIn',
						animateOut:'fadeOut',
						content:'请输入手机号',
						duration:2000,
						isCenter:true,
						});
						setTimeout(function(){
							$('.animated ').remove()
						},2000)
		}else if($('#selectArea').attr('sel')=='0086' && !/^1(3|4|5|7|8)\d{9}$/.test($('.phone').val()) ){
			$('body').toast({
						position:'fixed',
						animateIn:'fadeIn',
						animateOut:'fadeOut',
						content:'手机号格式不正确',
						duration:2000,
						isCenter:true,
						});
						setTimeout(function(){
							$('.animated ').remove()
						},2000)
		}else{
			this.areaId=$('#selectArea').attr('sel')
			this.phone=$('.phone').val()
			this.getCount()
			$('#pleaseShuru').hide()
		}
	},
	getCount:function(){
		var _this=this
		$.getJSON(href+'/prize/getLuckCount?activityCode=2&areaId='+this.areaId+'&phone='+this.phone,function(data){
				_this.countx=data.data
				$('#countx').html(data.data+'')
			
		})
	}
};
//域名
var href= 'http://120.77.81.25:8082'
var endnum = -1
var jiangpin = ['16','','88','500',' ','22','2.8','']

function roll() {
	
	lottery.times += 1;
	lottery.roll();
	//转动过程调用的是lottery的roll方法，这里是第一次调用初始化
	if(lottery.times > lottery.cycle + 10 && lottery.prize == lottery.index) {
		clearTimeout(lottery.timer);
		lottery.prize = -1;
		lottery.times = 0;
		setTimeout(function() {
			if(endnum == 4) {
				$('#nolucky').show()
			} else {
				$('#lucky').find('.jine').html(jiangpin[endnum])
				$('#lucky').show()

			}
			
			click=false;
		}, 1000)
		
	} else {
		if(lottery.times < lottery.cycle) {
			lottery.speed -= 10;
		} else if(lottery.times == lottery.cycle) {
			// var index = Math.random()*(lottery.count)|0; //中奖物品通过一个随机数生成
			aab = endnum
			lottery.stop(endnum)
		} else {
			if(lottery.times > lottery.cycle + 10 && ((lottery.prize == 0 && lottery.index == 7) || lottery.prize == lottery.index + 1)) {
				lottery.speed += 110;
			} else {
				lottery.speed += 20;
			}
		}
		if(lottery.speed < 40) {
			lottery.speed = 40;
		};
		//console.log(lottery.times+'^^^^^^'+lottery.speed+'^^^^^^^'+lottery.prize);

		lottery.timer = setTimeout(roll, lottery.speed); //循环调用

	}

	return false;
}

var click = false;
$(function(){ 
$("html, body").animate({
		scrollTop: 0 + "px"
	}, {
		duration: 300,
		easing: "swing"
	});
});
$(document).ready(function(){
	$(".fakeloader").fakeLoader({
                    timeToHide:100000,
                    bgColor:"#fff",
                    spinner:"spinner2"
                });
    });
window.onload = function() {
	 setTimeout(function(){
	$(".fakeloader").fadeOut()

	},2500)
	
	
	var img=new Image();
	var img2=new Image();
    img.src='img/one@3x.png';
	img2.src='img/背景@3x-2.png';

  if(img.width!=0 && img2.width!=0){
  	
  }else{
     
  }
	
	
	
	if(getparams('isWechat')){
		$.getJSON(href+'/wechat/jsapi/sign?url='+escape(location.href),function(data){
		wx.config({
		    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
		    appId: data.data.appid, // 必填，企业号的唯一标识，此处填写企业号corpid
		    timestamp: data.data.timestamp, // 必填，生成签名的时间戳
		    nonceStr: data.data.noncestr, // 必填，生成签名的随机串
		    signature: data.data.signature,// 必填，签名，见附录1
		    jsApiList: ['onMenuShareTimeline','onMenuShareAppMessage'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
		});
		wx.ready(function(){
   wx.onMenuShareTimeline({
    title: '飞带社区抽奖专区', // 分享标题
    link: window.location.href+'?isWechat=1', // 分享链接，该链接域名必须与当前企业的可信域名一致
    imgUrl: 'http://oss-cdn.feedel.net/images/logo.png', // 分享图标
    success: function () {
        // 用户确认分享后执行的回调函数
        if(lottery.phone.length==0){
        	
        }else{
        lottery.shareOK()
        }

        $('#pleaseShare').hide()
        $('#wechatshow').hide()
    },
    cancel: function () {
        // 用户取消分享后执行的回调函数
    }
});
wx.onMenuShareAppMessage({
    title: '飞带社区抽奖专区', // 分享标题
    desc: '狗年开运，飞带送你欧洲双人游，快来试试您的运气吧~', // 分享描述
    link: window.location.href+'?isWechat=1', // 分享链接，该链接域名必须与当前企业的可信域名一致
    imgUrl: 'http://oss-cdn.feedel.net/images/logo.png', // 分享图标
    type: '', // 分享类型,music、video或link，不填默认为link
    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
    success: function () {
        // 用户确认分享后执行的回调函数
        if(lottery.phone.length==0){
        	
        }else{
        lottery.shareOK()
        }
        $('#pleaseShare').hide()
        $('#wechatshow').hide()
    },
    cancel: function () {
        // 用户取消分享后执行的回调函数
    }
});
});
	})
	}else{
		

	}
	
	
	
	//获取区号
		$.getJSON('./js/fb_sys_dict.json',function(arr){
		var ass=[]
		for(var i=0;i<arr.length;i++){
			
				ass.push({
					value:arr[i].lable,
					num:arr[i].value,
				})
			
		}

	var mobileSelect1 = new MobileSelect({
    trigger: '#selectArea',
    title: '区号',
    wheels: [
                {data: ass}
            ],
    position:[0], //初始化定位 打开时默认选中的哪个 如果不填默认为0
    transitionEnd:function(indexArr, data){
        //console.log(data);
    },
    callback:function(indexArr, data){

      	$('#selectArea').attr('sel',data[0].num)
    }
});
	})
	
	
	
	setInterval(function(){
		
	},500)
	

};

function choujiang(){
//	$('#shuru').show()

if(lottery.phone.length==0){
	
	$('#pleaseShuru').show()
	
}else{
	if($('#countx').html()==0){
		$('#pleaseShare').show()
	}else{
		
		if(click){
		return false
		}
		click = true;
				//转圈过程不响应click事件，会将click置为false
		$.ajax({
			url: href+'/prize/'+lottery.areaId+'/2/' + lottery.phone, //数据的接口的路径
					dataType: 'json',
					type: "get", //请求的方式  默认是get
					async: true, //是否是异步，默认是异步
					timeout: 10000,
					success: function(res) {
						if(res.code == 200) {
							lottery.speed=100
							lottery.init('lottery');
							lottery.getCount()
							if(res.data == 8) {
								endnum = 4
							}else if(res.data == 1) {
								endnum = 3
							}else if(res.data == 2) {
								endnum = 6
							}else if(res.data == 3) {
								endnum = 0
							}else if(res.data == 4) {
								endnum = 5
							} else if(res.data == 5) {
								endnum = 2
							}
							setTimeout(function() {
								roll();
								
							}, 500)

						} else {
							$('#pleaseShare').show()
						}
					}
				});
	}
}


}

function getparams(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");

	var r = decodeURIComponent(window.location.search).substr(1).match(reg);
	if(r != null) return unescape(r[2]);
	return null;
	}



