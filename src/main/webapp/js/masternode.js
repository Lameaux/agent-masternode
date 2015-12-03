function uploadFile(file) {
	$('#result').html('');

	var oMyForm = new FormData();
	oMyForm.append("file", file);

	$.ajax({
		url : '/upload',
		data : oMyForm,
		dataType : 'json',
		processData : false,
		contentType : false,
		type : 'POST',
		success : function(data) {
			$('#result').html('<img src="' + data.url + '" alt="' + data.fileName + '" />');
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