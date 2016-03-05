//Console 정의

//IE7 indexOf가 없음
if(!Array.prototype.indexOf){
    Array.prototype.indexOf = function(itemOfArray,startIdx){
        for(var i=(startIdx||0), len=this.length; i < len; i++){
            if(this[i] === itemOfArray) { return i;}
        }
        return -1;
    }
}
if(this.console === undefined){
    this.console = {
        log:function () {},
        error:function () {},
        debug:function () {},
        warn:function () {}
    }
}

var cmsUtil = (function(){
    var isString = function (s) {
        return typeof s === 'string';
    };
    var isNumber = function(s){
        return typeof s === 'number';
    }
    var format = function(format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/{(\d+)}/g, function(match, number) { 
          return typeof args[number] != 'undefined'
            ? args[number] 
            : match
          ;
        });
    };
    var isEmpty = function(e){
            if( Object.prototype.toString.call( e ) === '[object Array]' ) {
                if(e.length === 0) return true;
                return false;
            }
            if( Object.prototype.toString.call( e ) === '[object Object]' ){
                for(var p in e){
                    if(e.hasOwnProperty(p)) {
                        return false;
                    }
                }
                return true;
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
        };
    var Stack = function(){
        this.array = [];
        this.pop = function(){ return this.array.pop();}
        this.push = function(item) { this.array.push(item);}
        this.isEmpty = function(){return this.array.length===0;}
    }
    return {
        isEmpty : isEmpty,
        trim : function(s){
          if(!isString(s)) return s;
          return s.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
        },
        contains: function (str, key) {
            if (!isString(str) || !isString(key)) return false;
            if (str === '' || key  === '') return false;
            return str.indexOf(key) > -1;
        },
        startsWith: function (str, prefix){
            if (!isString(str) || !isString(prefix)) return false;
            if (str === '' || prefix === '') return false;
            return str.lastIndexOf(prefix, 0) === 0;    
        },
        endsWith: function(str, suffix) {
            if (!isString(str) || !isString(suffix)) return false;
            if (str === '' || suffix === '') return false;
            return str.indexOf(suffix, str.length - suffix.length) !== -1;
        },
        format: format, // format('{0}-{1}-{2}','a',1,'3')=> a-1-3
        dateFormat : function dateFormat(format, date){
            var weekName1 = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"],
                weekName2 = ["일", "월", "화", "수", "목", "금", "토"],
                d = date,
                zf = function(n,len){
                    var zero = '', n = n+'';
                    if(n.length >= len) return n;
                    for(var i=0; i < (len-n.length);i++) zero+='0';
                    return zero+n;
                };
            return format.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
                switch ($1) {
                    case "yyyy": return d.getFullYear();
                    case "yy": return zf( (d.getFullYear() % 1000), 2);
                    case "MM": return zf( (d.getMonth() + 1), 2) ;
                    case "dd": return zf( d.getDate(), 2) ;
                    case "E": return weekName1[d.getDay()];
                    case "e": return weekName2[d.getDay()];
                    case "HH": return zf( d.getHours(), 2);
                    case "hh": return zf( ((h = d.getHours() % 12) ? h : 12), 2);
                    case "mm": return zf( d.getMinutes(), 2);
                    case "ss": return zf(  d.getSeconds(), 2) ;
                    case "a/p": return d.getHours() < 12 ? "오전" : "오후";
                    default: return $1;
                }
            }); 
        },
        today : function(dateformat){
          return this.isEmpty(dateformat) ? this.dateFormat('yyyy-MM-dd', new Date()) : this.dateFormat(dateformat, new Date());
        },
        getDiffDay : function (fromDate, toDate) {
                if(!isString(fromDate) || !isString(toDate)) return undefined;
                var fromDate = fromDate.replace(/[-.\/]/gi, ''),// '-'가 있다면 제거
                    toDate   = toDate.replace(/[-.\/]/gi, '');
                if(fromDate.length === 8 && toDate.length == 8){
                    var date1  = new Date(fromDate.substring(0,4), fromDate.substring(4,6)-1, fromDate.substring(6,8) ),
                        date2  = new Date(toDate.substring(0,4), toDate.substring(4,6)-1,toDate.substring(6,8));

                    return Math.abs(Math.floor(( date2 - date1 )/86400000));
                }
                return undefined;
        },        
        dispRettNo: function(s){
            if(s.length === 14){
                return s.substring(0,4)+'-'+s.substring(4,8)+'-'+s.substring(8);
            }
            return s;
        },
        dispDate : function(s){
            if(s.length === 8){
                return s.substring(0,4)+'-'+s.substring(4,6)+'-'+s.substring(6);
            }
            return s;            
        },
        dispMoney : function (str){
            var str = str;
            if(isNumber(str)){
                str = str + '';
            }
            var chars = str.split("").reverse();
            var reChars = [];

            for (var i = 0; chars.length > i; i++){
                 if( i != 0 &&( ( i+1 ) % 3) == 1){
                    reChars[reChars.length] = ",";
                 }
                reChars[reChars.length] = chars[i];
            }

            return reChars.reverse().join("");
        },
        getSettings : function(options, defaults){
               var options = options || {},
                   defaults  = defaults || {},
                   settings = {};
               for(var opt in options){
                   if(options.hasOwnProperty(opt)){
                    settings[opt] = options[opt];
                   }
               }
               for(var opt in defaults){
                   if(defaults.hasOwnProperty(opt) && !options.hasOwnProperty(opt)){
                       settings[opt] = defaults[opt];
                   }
               }
               return settings;
        },
        newStack : function(){
            return new Stack();
        }
    };    
})();