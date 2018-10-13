var lottery = {
	index: 1, //当前转动到哪个位置，起点位置
	count: 8, //总共有多少个位置
	timer: 'acc', //setTimeout的ID，用clearTimeout清除
	speed: 20, //初始转动速度
	times: 0, //转动次数
	cycle: 50, //转动基本次数：即至少需要转动多少次再进入抽奖环节
	prize: -1, //中奖位置
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
	}
};
//域名
var href= ''
var endnum = -1
var jiangpin = ['', '666元现金红包', '行李箱', '星巴克储蓄卡', '充电宝', '66元现金', '耳麦', '8.8元现金']

function roll() {

	lottery.times += 1;
	lottery.roll();
	//转动过程调用的是lottery的roll方法，这里是第一次调用初始化
	if(lottery.times > lottery.cycle + 10 && lottery.prize == lottery.index) {
		clearTimeout(lottery.timer);
		lottery.prize = -1;
		lottery.times = 0;
		setTimeout(function() {
			if(endnum == 0) {
				$('#zhongle').find('h2').html('好可惜噢！')
				$('#zhongle').find('p').html('没有中奖~么么哒')
				$('#zhongle').find('.btn').html('可以分享给您的领队/导游朋友一起参与噢！')
			} else {
				$('#zhongle').find('h2').html('超幸运的！中奖啦！')
				$('#zhongle').find('p').html(jiangpin[endnum])
				$('#zhongle').find('.btn').html('工作人员将在1-2个工作日内与您取得联系')
			}
			$('#zhongle').show()
		}, 1000)
		//click=false;
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
window.onload = function() {

	lottery.init('lottery');
	$("#start").click(function() {

		if($('.tel').val().length == 0) {
			alert('请输入手机号')
		} else if(!/^1(3|4|5|7|8)\d{9}$/.test($('.tel').val())) {
			alert('手机号码格式不正确')
		} else {
			if(click) { //click控制一次抽奖过程中不能重复点击抽奖按钮，后面的点击不响应
				return false;
			} else {
				lottery.speed = 100;
				click = true;
				//转圈过程不响应click事件，会将click置为false
				$.ajax({
					url: href+'/prize/0086/1/' + $('.tel').val(), //数据的接口的路径
					dataType: 'json',
					type: "get", //请求的方式  默认是get
					async: true, //是否是异步，默认是异步
					timeout: 10000,
					success: function(res) {
						if(res.code == 200) {
							if(res.data == 8) {
								endnum = 0
							} else {
								endnum = res.data
							}

							$('#shuru').hide()
							click=false
							setTimeout(function() {
								roll();
								
							}, 500)

						} else{
							click=false
							if(res.code==601){
								alert(res.message)
								window.location.href='about.html'
								
							}else{
								alert(res.message)
							}
							
						}
					}
				});

				//一次抽奖完成后，设置click为true，可继续抽奖
				return false;
			}
		}

	});
};

function choujiang(){
	
	$('#shuru').show()
}
