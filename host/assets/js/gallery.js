function checkValidation()
{
	 
	if(document.getElementById('subcategory').value=="")
	{
		alert("Please Select Sub Category.!");
		 
		document.getElementById('subcategory').focus();		 
		return false;
	}
	if(document.getElementById('image_name').value=="")
	{
		alert("Please enter image name.!");
		 
		document.getElementById('image_name').focus();		 
		return false;
	}
	if(document.getElementById('image').value=="")
	{
		alert("Please select image.!");
		 
		document.getElementById('image').focus();		 
		return false;
	}
	
	return true;
} 