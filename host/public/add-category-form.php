<?php
	include_once('includes/connect_database.php'); 
	include_once('functions.php'); 
?>

	<?php 
		if(isset($_POST['btnAdd'])){
			$category_name = $_POST['category_name'];
			
			// get image info
			$menu_image = $_FILES['category_image']['name'];
			$image_error = $_FILES['category_image']['error'];
			$image_type = $_FILES['category_image']['type'];
			
			// create array variable to handle error
			$error = array();
			
			if(empty($category_name)){
				$error['category_name'] = " <span class='label label-danger'>Must Insert!</span>";
			}
			
			// common image file extensions
			$allowedExts = array("gif", "jpeg", "jpg", "png");
			
			// get image file extension
			error_reporting(E_ERROR | E_PARSE);
			$extension = end(explode(".", $_FILES["category_image"]["name"]));
					
			if($image_error > 0){
				$error['category_image'] = " <span class='label label-danger'>You're not insert images!!</span>";
			}else if(!(($image_type == "image/gif") || 
				($image_type == "image/jpeg") || 
				($image_type == "image/jpg") || 
				($image_type == "image/x-png") ||
				($image_type == "image/png") || 
				($image_type == "image/pjpeg")) &&
				!(in_array($extension, $allowedExts))){
			
				$error['category_image'] = " <span class='label label-danger'>Image type must jpg, jpeg, gif, or png!</span>";
			}
			
			if(!empty($category_name) && empty($error['category_image'])){
				
				// create random image file name
				$string = '0123456789';
				$file = preg_replace("/\s+/", "_", $_FILES['category_image']['name']);
				$function = new functions;
				$menu_image = $function->get_random_string($string, 4)."-".date("Y-m-d").".".$extension;
					
				// upload new image
				$upload = move_uploaded_file($_FILES['category_image']['tmp_name'], 'upload/category/'.$menu_image);
		
				// insert new data to menu table
				$sql_query = "INSERT INTO tbl_category (category_name, category_image)
						VALUES(?, ?)";
				
				$upload_image = $menu_image;
				$stmt = $connect->stmt_init();
				if($stmt->prepare($sql_query)) {	
					// Bind your variables to replace the ?s
					$stmt->bind_param('ss', 
								$category_name, 
								$upload_image
								);
					// Execute query
					$stmt->execute();
					// store result 
					$result = $stmt->store_result();
					$stmt->close();
				}
				
				if($result){
					$error['add_category'] = "<div class='card-panel teal lighten-2'>
											    <span class='white-text text-darken-2'>
												   New Category Added Successfully
											    </span>
											</div>";
				}else{
					$error['add_category'] = "<div class='card-panel red darken-1'>
											    <span class='white-text text-darken-2'>
												    Added Failed
											    </span>
											</div>";
				}
			}
			
		}

		if(isset($_POST['btnCancel'])){
			header("location: category.php");
		}

	?>

	<!-- START CONTENT -->
    <section id="content">

        <!--breadcrumbs start-->
        <div id="breadcrumbs-wrapper" class=" grey lighten-3">
          	<div class="container">
            	<div class="row">
              		<div class="col s12 m12 l12">
               			<h5 class="breadcrumbs-title">Add New Category</h5>
		                <ol class="breadcrumb">
		                  <li><a href="dashboard.php">Dashboard</a>
		                  </li>
		                  <li><a href="#" class="active">Add New Category</a>
		                  </li>
		                </ol>
              		</div>
            	</div>
          	</div>
        </div>
        <!--breadcrumbs end-->

         <!--start container-->
        <div class="container">
          	<div class="section">
				<div class="row">
		        	<div class="col s12 m12 l12">
		        		<div class="card-panel">
		                	<div class="row">
		                 		<form method="post" class="col s12" enctype="multipart/form-data">
		                  			<div class="row">
		                    			<div class="input-field col s12">   
		                    				<?php echo isset($error['add_category']) ? $error['add_category'] : '';?>       
											
											<div class="row">
						                      <div class="input-field col s12">
						                        <input type="text" name="category_name" id="category_name" required/>
						                        <label for="category_name">Category Name</label><?php echo isset($error['category_name']) ? $error['category_name'] : '';?>
						                      </div>
						                    </div> 	

											<div class="file-field input-field col s12">
											<?php echo isset($error['category_image']) ? $error['category_image'] : '';?>
	                                            <input class="file-path validate" type="text" disabled/>
	                                            <div class="btn">
	                                                <span>Image</span>
	                                                <input type="file" name="category_image" id="category_image" value="" required/>
	                                            </div>
	                                        </div>

	                                        <button class="btn cyan waves-effect waves-light right"
	                                                type="submit" name="btnAdd">Submit
	                                            <i class="mdi-content-send right"></i>
	                                        </button>	                                        

										</div>
						            </div>
						        </form>
						    </div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</section> 

<br>
<br>
<br>
<br>
<br>
<br>
<?php include_once('includes/close_database.php'); ?>