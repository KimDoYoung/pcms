//-------------------------------------------------------------
//  Created by: Youngsoon Bang / 2014.01.06
//  Mail: youngsoon.bang@gmail.com
//  Site: www.intous.kr
//-------------------------------------------------------------

(function(){
  CKEDITOR.dialog.add( 'geogebra', function( editor ){
    var id = editor.id;
    return{
      title : 'GeoGebra Editor',
      minWidth : 910,
      minHeight : 450,
      //buttons: [],
      contents :
        [
          {
            id : 'iframe',
            label : 'GeoGebra Editor',
            expand : true,
            elements :
              [
                {
				          type : 'html',
				          id : 'pageGeoGebraEmbed',
				          label : 'GeoGebra Editor',
				          html : '<div style="width:920px;height:460px"><iframe src="'+CKEDITOR.plugins.getPath('geogebra')+'dialogs/editor.html" frameborder="0" name="iframeGeoGebraEditor'+id+'" id="iframeGeoGebraEditor'+id+'" allowtransparency="1" style="width:920px;height:460px;margin:0;padding:0;" scrolling="no"></iframe></div>'
				        }
              ]
          }
        ],
		  onOk : function(){
			var frame = document.getElementById('iframeGeoGebraEditor'+id).contentWindow;
			//frame.saveImage(editor);
      frame.saveLaTeXAndUpdate(editor);
			return false;
      },
		  onHide : function(){
			var frame = document.getElementById('iframeGeoGebraEditor'+id);
			frame.src = CKEDITOR.plugins.getPath('geogebra') +'dialogs/editor.html';
		  }
    };
  });
})();



