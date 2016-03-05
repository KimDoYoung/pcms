//-------------------------------------------------------------
//  Created by: Youngsoon Bang / 2014.01.06
//  Mail: youngsoon.bang@gmail.com
//  Site: www.intous.kr
//-------------------------------------------------------------

(function(){
  CKEDITOR.dialog.add( 'svgedit', function( editor ){
    var id = editor.id;
    return{
      title : 'SVG Editor',
      minWidth : 910,
      minHeight : 450,
      //buttons: [],
      contents :
        [
          {
            id : 'iframe',
            label : 'SVG Editor',
            expand : true,
            elements :
              [
                {
				          type : 'html',
				          id : 'pageSvgEditEmbed',
				          label : 'SVG Editor',
				          html : '<div style="width:920px;height:550px"><iframe src="' + CKEDITOR.plugins.getPath('svgedit') + 'dialogs/embedapi.html" frameborder="0" name="iframeSvgEditEditor' + id + '" id="iframeSvgEditEditor' + id + '" allowtransparency="1" style="width:920px;height:550px;margin:0;padding:0;" scrolling="no"></iframe></div>'
				        }
              ]
          }
        ],
		  onOk : function(){
			var frame = document.getElementById('iframeSvgEditEditor' + id).contentWindow;
			//frame.saveImage(editor);
      //frame.saveLaTeXAndUpdate(editor);
      frame.saveSvgAndUpdate(editor);
			return false;
      },
		  onHide : function(){
			var frame = document.getElementById('iframeSvgEditEditor' + id);
			frame.src = CKEDITOR.plugins.getPath('svgedit') + 'dialogs/embedapi.html';
		  }
    };
  });
})();



