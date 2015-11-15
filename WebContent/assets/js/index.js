$(document).ready(init);

function init(){
	
	$layoutQtr = $('.layout-qtr');
	$layoutQtr.width(function(){
		
		return $layoutQtr.width() - 1;
	});
}