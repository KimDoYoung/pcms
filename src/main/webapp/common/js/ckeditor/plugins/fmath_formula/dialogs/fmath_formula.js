//-------------------------------------------------------------
//	Created by: Ionel Alexandru
//	Mail: ionel.alexandru@gmail.com
//	Site: www.fmath.info
//---------------------------------------------------------------

(function()
{

	CKEDITOR.dialog.add( 'fmath_formula', function( editor )
	{
	      var id = editor.id;
              return {
                 title : '수식편집기',
                 minWidth : 910,
                 minHeight : 480,
                 //buttons: [],
                 contents :
                       [
                          {
                             id : 'iframe',
                             label : 'JMES 수식편집기',
                             expand : true,
                             elements :
                                   [
                                      {
				       type : 'html',
				       id : 'pageMathMLEmbed',
				       label : '수식편집기(Mathml Edito)r',
				       html : '<div style="width:900px;height:470px"><iframe src="'+ CKEDITOR.plugins.getPath('fmath_formula') +'dialogs/editor.html" frameborder="0" name="iframeMathmlEditor'+id+'" id="iframeMathmlEditor'+id+'" allowtransparency="1" style="width:900px;height:470px;margin:0;padding:0;" scrolling="no"></iframe></div>'
				      }
                                   ]
                          }
                       ],
		onOk : function()
		{
			var frame = document.getElementById('iframeMathmlEditor'+id).contentWindow;
			//frame.saveImage(editor);
            frame.saveMathMLAndUpdate(editor);
			return false;
                 },
		onHide : function()
		{
			var frame = document.getElementById('iframeMathmlEditor'+id);
			frame.src = CKEDITOR.plugins.getPath('fmath_formula') +'dialogs/editor.html';
		}
              };
        } );

})();



