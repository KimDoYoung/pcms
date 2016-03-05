//-------------------------------------------------------------
//	Created by: Youngsoon Bang / 2014.01.06
//	Mail: youngsoon.bang@gmail.com
//	Site: www.intous.kr
//-------------------------------------------------------------

(function(){
	var svgedit_instances = 1;
	var svgedit_nbFlash = 0;
	var svgedit_flashMathML = new Array();
	var svgedit_selectedElement = "";
	var svgedit_currentElement = "";
	var svgedit_newDialog = new CKEDITOR.dialogCommand('svgedit');
	CKEDITOR.plugins.add('svgedit',{
		init: function(editor){
			CKEDITOR.dialog.add('svgedit',this.path + 'dialogs/svgedit.js');
			editor.addCommand('svgedit',svgedit_newDialog);
			editor.ui.addButton('svgedit', {
				label:'SVG Editor',
				command: 'svgedit',
				icon: this.path + 'svgedit.jpg'
			});
			editor.on('selectionChange',function(evt){
				/*
				 * Despite our initial hope, document.queryCommandEnabled() does not work
				 * for this in Firefox. So we must detect the state by element paths.
				 */
				//var command = editor.getCommand( 'svgedit' )
				var element = evt.data.path.lastElement.getAscendant('img', true);
				svgedit_currentElement = "";
				if(element!=null){
					var id = element.getAttribute("id");
					if(id!=null && id.indexOf("SvgEdit")>=0){
						svgedit_currentElement = id;
					}
				}
			});
		},
		addMathML : function(m){
			svgedit_nbFlash =svgedit_nbFlash + 1;
			var newName = "SvgEdit" + svgedit_nbFlash;
			svgedit_flashMathML[newName] = m;
			return newName;
		},
		updateMathML : function(id, m){
			svgedit_flashMathML[id] = m;
		},
		getSelected : function(){
			return svgedit_currentElement;
		},
		getCurrentMathML : function(){
			return svgedit_flashMathML[svgedit_currentElement];
		},
		getMathML : function(name){
			return svgedit_flashMathML[name];
		}
	});
})();