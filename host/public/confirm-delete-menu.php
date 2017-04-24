<?php
	include_once('includes/connect_database.php'); 
?>

	<?php 
		
		if(isset($_POST['btnDelete'])){
			if(isset($_GET['id'])){
				$ID = $_GET['id'];
			}else{
				$ID = "";
			}
		
			// get image file from menu table
			$sql_query = "SELECT radio_image 
					FROM tbl_radio 
					WHERE id = ?";
			
			$stmt = $connect->stmt_init();
			if($stmt->prepare($sql_query)) {	
				// Bind your variables to replace the ?s
				$stmt->bind_param('s', $ID);
				// Execute query
				$stmt->execute();
				// store result 
				$stmt->store_result();
				$stmt->bind_result($radio_image);
				$stmt->fetch();
				$stmt->close();
			}
			
			// delete image file from directory
			$delete = unlink('upload/'."$radio_image");
			//$delete = unlink('upload/thumbs/'."$radio_image");
			
			// delete data from menu table
			$sql_query = "DELETE FROM tbl_radio 
					WHERE id = ?";
			
			$stmt = $connect->stmt_init();
			if($stmt->prepare($sql_query)) {	
				// Bind your variables to replace the ?s
				$stmt->bind_param('s', $ID);
				// Execute query
				$stmt->execute();
				// store result 
				$delete_result = $stmt->store_result();
				$stmt->close();
			}
				
			// if delete data success back to reservation page
			if($delete_result){
				header("location: radio.php");
			}
		}		

		if(isset($_POST['btnNo'])){
			header("location: radio.php");
		}

	?>
	<!-- START CONTENT -->
    <section id="content">

        <!--breadcrumbs start-->
        <div id="breadcrumbs-wrapper" class=" grey lighten-3">
          	<div class="container">
            	<div class="row">
              		<div class="col s12 m12 l12">
               			<h5 class="breadcrumbs-title">Delete Radio</h5>
		                <ol class="breadcrumb">
		                  <li><a href="dashboard.php">Dashboard</a>
		                  </li>
		                  <li><a href="#" class="active">Delete Radio</a>
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
		                 		<form method="post" class="col s12">
		                  			<div class="row">
		                    			<div class="input-field col s12">  
		                    				<p>Are you sure want to delete this Radio?</p>
		                    				<button class="btn cyan waves-effect waves-light"
	                                                type="submit" name="btnDelete">Delete
	                                        </button>

		                    				<button class="btn cyan waves-effect waves-light"
	                                                type="submit" name="btnNo">Cancel
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

<?php include_once('includes/close_database.php'); ?>