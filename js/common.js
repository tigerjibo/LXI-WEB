function linkTo(url){
	window.location.href=url;
}

//验证码的刷新
function refreshImage(thisObject){
		var el = thisObject;
		el.src = el.src + '?';
	}
