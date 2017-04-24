<?php
	include_once('includes/connect_database.php'); 
	include_once('functions.php'); 
	require_once("thumbnail_images.class.php");
?>

	<?php 
		$sql_query = "SELECT cid, category_name 
			FROM tbl_category 
			ORDER BY cid ASC";
				
		$stmt_category = $connect->stmt_init();
		if($stmt_category->prepare($sql_query)) {	
			// Execute query
			$stmt_category->execute();
			// store result 
			$stmt_category->store_result();
			$stmt_category->bind_result($category_data['cid'], 
				$category_data['category_name']
				);		
		}
		
			
		//$max_serve = 10;
			
		if(isset($_POST['btnAdd'])){
			$radio_name = $_POST['radio_name'];
			$cid = $_POST['cid'];
			$radio_url = $_POST['radio_url'];
				
			// get image info
			$radio_image = $_FILES['radio_image']['name'];
			$image_error = $_FILES['radio_image']['error'];
			$image_type = $_FILES['radio_image']['type'];
			
				
			// create array variable to handle error
			$error = array();
			
			if(empty($radio_name)){
				$error['radio_name'] = " <span class='label label-danger'>Required, please fill out this field!!</span>";
			}
				
			if(empty($cid)){
				$error['cid'] = " <span class='label label-danger'>Required, please fill out this field!!</span>";
			}				
				
			if(empty($radio_url)){
				$error['radio_url'] = " <span class='label label-danger'>Required, please fill out this field!!</span>";
			}			
			
			// common image file extensions
			$allowedExts = array("gif", "jpeg", "jpg", "png");
			
			// get image file extension
			error_reporting(E_ERROR | E_PARSE);
			$extension = end(explode(".", $_FILES["radio_image"]["name"]));
					
			if ($image_error > 0) {
				$error['radio_image'] = " <span class='label label-danger'>Image Not Uploaded!!</span>";
			} else if(!(($image_type == "image/gif") || 
				($image_type == "image/jpeg") || 
				($image_type == "image/jpg") || 
				($image_type == "image/x-png") ||
				($image_type == "image/png") || 
				($image_type == "image/pjpeg")) &&
				!(in_array($extension, $allowedExts))){
			
				$error['radio_image'] = "Image type must jpg, jpeg, gif, or png!";
			}
				
			if (!empty($radio_name) && 
				!empty($cid) && 
				!empty($radio_url) && 
				 empty($error['radio_image'])) {		

				// create random image file name
				$string = '0123456789';
				$file = preg_replace("/\s+/", "_", $_FILES['radio_image']['name']);
				$function = new functions;
				$radio_image = $function->get_random_string($string, 4)."-".date("Y-m-d").".".$extension;
					
				// upload new image
				$unggah = 'upload/'.$radio_image;
				$upload = move_uploaded_file($_FILES['radio_image']['tmp_name'], $unggah);
		
				// insert new data to menu table
				$sql_query = "INSERT INTO tbl_radio (radio_name, category_id, radio_url, radio_image)
						VALUES(?, ?, ?, ?)";
						
				$upload_image = $radio_image;
				$stmt = $connect->stmt_init();
				if($stmt->prepare($sql_query)) {	
					// Bind your variables to replace the ?s
					$stmt->bind_param('ssss', 
								$radio_name, 
								$cid, 
								$radio_url, 
								$upload_image
								);
					// Execute query
					$stmt->execute();
					// store result 
					$result = $stmt->store_result();
					$stmt->close();
				}			
				
				if($result){
					$error['add_menu'] = "<div class='card-panel teal lighten-2'>
											    <span class='white-text text-darken-2'>
												    New Radio Added Successfully
											    </span>
											</div>";
				}else {
					$error['add_menu'] = "<div class='card-panel red darken-1'>
											    <span class='white-text text-darken-2'>
												    Added Failed
											    </span>
											</div>";
				}
			}
				
			}
	?>

	<!-- START CONTENT -->
    <section id="content">

        <!--breadcrumbs start-->
        <div id="breadcrumbs-wrapper" class=" grey lighten-3">
          	<div class="container">
            	<div class="row">
              		<div class="col s12 m12 l12">
               			<h5 class="breadcrumbs-title">Add New Radio Station</h5>
		                <ol class="breadcrumb">
		                  <li><a href="dashboard.php">Dashboard</a>
		                  </li>
		                  <li><a href="#" class="active">Add New Radio Station</a>
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
		                  			<?php echo isset($error['add_menu']) ? $error['add_menu'] : '';?>   
		                    			<div class="input-field col s12">   
											<div class="row">
						                      <div class="input-field col s12">
						                        <input type="text" name="radio_name" id="radio_name" placeholder="Radio Name" required/>
						                        <label for="radio_name">Radio Name</label><?php echo isset($error['radio_name']) ? $error['radio_name'] : '';?>
						                      </div>
						                    </div>   

						                    <div class="row">
							                    <div class="input-field col s12">
	                                            <select name="cid">
	                                                <?php while($stmt_category->fetch()){ ?>
															<option value="<?php echo $category_data['cid']; ?>"><?php echo $category_data['category_name']; ?></option>
													<?php } ?>
	                                            </select>
	                                            <label>Category</label><?php echo isset($error['cid']) ? $error['cid'] : '';?></div>	
                                            </div>

											<div class="row">
						                      <div class="input-field col s12">
						                        <input type="text" name="radio_url" id="radio_url" placeholder="ex : http://live.radiorodja.com/ or http://202.147.199.100:8000/" required/>
						                        <label for="radio_url">Radio Stream Url</label><?php echo isset($error['radio_url']) ? $error['radio_url'] : '';?>
						                      </div>
						                    </div>

											<div class="file-field input-field col s12">
											<?php echo isset($error['radio_image']) ? $error['radio_image'] : '';?>
	                                            <input class="file-path validate" type="text" disabled/>
	                                            <div class="btn">
	                                                <span>Image</span>
	                                                <input type="file" name="radio_image" id="radio_image" value="" required/>
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
			
<?php 
	$stmt_category->close();
	include_once('includes/close_database.php'); ?>