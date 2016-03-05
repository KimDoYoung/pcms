/**
 * 공통으로 사용되는 라이브러리성 함수들
 */
//"ABC".replaceAll(..);
if (typeof String.prototype.replaceAll != 'function') {
	String.prototype.replaceAll = function(search, replace){
		if(!replace) return this;
		return this.replace(new RegExp('[' + search + ']', 'g'), replace);
	};
}
//"ABC".startsWith(..);
if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function (str){
	  return this.slice(0, str.length) == str;
	};
}
//"ABC".endsWith(..);
if (typeof String.prototype.endsWith != 'function') {
	String.prototype.endsWith = function (str){
	   return this.slice(-str.length) == str;
	};
}
//format : "{0} is dead, but {1} is alive! {0} {2}".format("ASP", "ASP.NET")
if (typeof String.prototype.format != 'function') {
	String.prototype.format = function() {
		var args = arguments;
		return this.replace(/{(\d+)}/g, function(match, number) {
			return typeof args[number] != 'undefined' ? args[number] : match;
		});
	};
}

/**
 * 객체가 비어 있는지 체크한다.
 * "", null, undefined가 empty true이다
 * @param e
 * @returns {Boolean}
 */
function isEmpty(e) {
	if( Object.prototype.toString.call( e ) === '[object Array]' ) {
	    if(e.length === 0) return true;
	    return false;
	}
    if((typeof e) == 'undefined' ) return true;
    switch(e) {
        case "":
        case null:
        case typeof this == "undefined":
            return true;
        default : 
        	return false;
    }
}

/**
 * jQuery ajax를 호출한다.
 * @param options ajax호출에 필요한 내용을 담고 있는 객체이다
 */
function optionAjax(options){

	var defaults = {
        url  : null,
        dataType : 'json',
        data : {},
        success : function(data){
          console.log(data);
        },
        beforeSend : function(){
            console.log('show waiting dialog');
            $.blockUI({ message: null });
        },
        complete : function(data) {
            console.log("ajaxCall completed");
            $.unblockUI();
        },
        error : function(xhr, status, error) {
          var msg = "ajax error"+":"+xhr+","+status+","+error;
          console.log(msg);
          alert(msg);
        }
    };
	var settings = $.extend({}, defaults, options);
	
	if(settings.url == null ){
		alert('url is null or data');
		return;
	}

	jQuery.ajax({
        type     : "POST",
        url      : settings.url,
        data     : settings.data,
        dataType : settings.dataType,
        success  : settings.success,
        complete : settings.complete,
        error    : settings.error,
        beforeSend : settings.beforeSend
  	});
}
/**
 * sql문장을 인자로 ajax를 호출한다.
 *  var ajax =  sqlAjax("select campus_id, nm from campus where branch_id = "+ $branch_id +" order by nm ")
 *  ajax.done(function(result){ //your code  });
 * @param sql
 * @returns
 */
function sqlAjax(sql){
	return $.ajax({
				type : 'POST',
				url : '/ajax/sql',
				data : {'sql' : sql},
				dataType : 'json',
				error :function(xhr, status, error) {
		              var msg = "ajax error"+":"+xhr+","+status+","+error;
		              console.log('sql:['+sql+']');
		              //alert(msg);
		          }
			});
}

/**
 * popup 윈도우
 * @param url
 * @param option
 * @param data get방식으로 넘길 namvalue
 */
function openWindow(url,options,data){
    var defaults = {
        title: 'popup'
        ,w : 500
        ,h : 300
    };
    var settings = $.extend({},defaults, options || {});
    var url2 = url + "?" + data;
    var w = settings.w, h=settings.h, left = (screen.width/2)-(w/2),
        top = (screen.height/2)-(h/2) ;
    return   window.open(url2,settings.title,'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);

}

/**
 * jmes 프로젝트에서ㅏ 사용하는 유틸리티 함수들을 묶어 놓음
 * @type {{strip_semantics: Function, strip_tags: Function}}
 */
var MATHML = {
    strip_semantics : function(s){
        if(s == null || s == undefined) return '';
        return s.replace(/<\/semantics>/ig,'').replace(/<semantics>/ig,'');
    },
    strip_tags : function( html ) {
            var tmp = document.createElement("DIV");
            tmp.innerHTML = html;
            return tmp.textContent || tmp.innerText || "";
    },
    humanFileSize : function (bytes, si) {
        var thresh = si ? 1000 : 1024;
        if (bytes < thresh) return bytes + ' B';
        var units = si ? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'] : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
        var u = -1;
        do {
            bytes /= thresh;
            ++u;
        } while (bytes >= thresh);
        return bytes.toFixed(1) + ' ' + units[u];
    }
};
