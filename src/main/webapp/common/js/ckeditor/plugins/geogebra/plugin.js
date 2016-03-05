//-------------------------------------------------------------
//	Created by: Youngsoon Bang / 2014.01.06
//	Mail: youngsoon.bang@gmail.com
//	Site: www.intous.kr
//-------------------------------------------------------------

(function()
{

	var geogebra_instances = 1;
	var geogebra_nbFlash = 0;
	var geogebra_flashMathML = new Array();
	var geogebra_selectedElement = "";
	var geogebra_currentElement = "";
	
	var geogebra_newDialog = new CKEDITOR.dialogCommand('geogebra');
	
	CKEDITOR.plugins.add( 'geogebra',
	{
		init : function( editor )
		{

			CKEDITOR.dialog.add('geogebra', this.path + 'dialogs/geogebra.js');
			editor.addCommand('geogebra', geogebra_newDialog);
			editor.ui.addButton('geogebra', 
				{
					label:'Add GeoGebra',
					command: 'geogebra',
					icon: this.path + 'geogebra.jpg'
				});

			editor.on( 'selectionChange', function( evt )
			{
				/*
				 * Despite our initial hope, document.queryCommandEnabled() does not work
				 * for this in Firefox. So we must detect the state by element paths.
				 */
				//var command = editor.getCommand( 'geogebra' )
				var element = evt.data.path.lastElement.getAscendant( 'img', true );
				geogebra_currentElement = "";
				
				if(element!=null){
					var id = element.getAttribute("id");
					if(id!=null && id.indexOf("GeoGebra")>=0){
						geogebra_currentElement = id;
					}
				}
			} );
			
			//search the last Id
			
		},
		
		addMathML : function(m){
			geogebra_nbFlash =geogebra_nbFlash + 1;
			var newName = "GeoGebra" + geogebra_nbFlash;
			geogebra_flashMathML[newName] = m;
			return newName;
		},

		updateMathML : function(id, m){
			geogebra_flashMathML[id] = m;
		},

		getSelected : function(){
			return geogebra_currentElement;
		},
		
		getCurrentMathML : function(){
			return geogebra_flashMathML[geogebra_currentElement];
		},

		getMathML : function(name){
			return geogebra_flashMathML[name];
		}
		
	});
})();