function uploadFile(file) {
	$('#result').html('');

	var oMyForm = new FormData();
	oMyForm.append("file", file);

	$.ajax({
		url : '/upload',
		data : oMyForm,
		dataType : 'text',
		processData : false,
		contentType : false,
		type : 'POST',
		success : function(data) {
			$('#result').html(data);
		}
	});
}

$(function() {

	$('#upload_form').submit(function(){
		var files = $('#file')[0].files;
		if (files.length > 0) {
			uploadFile(files[0]);
		}
		return false;
	});
	
});