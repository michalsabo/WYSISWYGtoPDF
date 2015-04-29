/**
 * Created by michal on 26.4.2015.
 */
$(document).ready(function() {
    //set initial state.

    var html_original;

    $('#checkbox_parameters').change(function() {
            if($(this).is(":checked")) {
                html_original = $("#html").val();
                var html_processed = html_original;
                var parameters = $("#parameters").val();
                var array_parameters = parameters.split(";");
                for(var i = 0; i < array_parameters.length; i++) {
                        var result = array_parameters[i].split("=");
                        var variable = "$$" + result[0] + "$$";
                        var value = result[1];
                        html_processed = html_processed.replace(variable,value);
                }


                $('#html').data("wysihtml5").editor.setValue(html_processed);
            } else {
                $('#html').data("wysihtml5").editor.setValue(html_original);

            }

    });

    $( "#pdf_button" ).click(function() {
        var html_original = $("#html").val();
        html_original = encodeURIComponent(html_original);
        window.open("/../../documents/" + html_original,"_self")
    });
});