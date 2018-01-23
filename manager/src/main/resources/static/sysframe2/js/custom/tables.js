/*
 * 	Additional function for tables.html
 *	Written by ThemePixels	
 *	http://themepixels.com/
 *
 *	Copyright (c) 2012 ThemePixels (http://themepixels.com)
 *	
 *	Built for Amanda Premium Responsive Admin Template
 *  http://themeforest.net/category/site-templates/admin-templates
 */

jQuery(document).ready(function(){

    jQuery('#dyntable2').dataTable({
        "sPaginationType": "full_numbers",
        "aaSortingFixed": [[0,'asc']],
        "fnDrawCallback": function(oSettings) {
            jQuery('input:checkbox,input:radio').uniform();
            //jQuery.uniform.update();
        }
    });



    ///// TRANSFORM CHECKBOX AND RADIO BOX USING UNIFORM PLUGIN /////
   
});