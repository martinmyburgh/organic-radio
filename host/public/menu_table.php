<?php
	include_once('includes/connect_database.php');
	include_once('functions.php'); 
?>

	<?php 
		// create object of functions class
		$function = new functions;
		
		// create array variable to store data from database
		$data = array();
		
		if(isset($_GET['keyword'])){	
			// check value of keyword variable
			$keyword = $function->sanitize($_GET['keyword']);
			$bind_keyword = "%".$keyword."%";
		}else{
			$keyword = "";
			$bind_keyword = $keyword;
		}
			
		if(empty($keyword)){
			$sql_query = "SELECT id, radio_name, radio_image, category_name, radio_url 
					FROM tbl_radio m, tbl_category c
					WHERE m.category_id = c.cid  
					ORDER BY m.id DESC";
		}else{
			$sql_query = "SELECT id, radio_name, radio_image, category_name, radio_url 
					FROM tbl_radio m, tbl_category c
					WHERE m.category_id = c.cid AND radio_name LIKE ? 
					ORDER BY m.id DESC";
		}
		
		
		$stmt = $connect->stmt_init();
		if($stmt->prepare($sql_query)) {	
			// Bind your variables to replace the ?s
			if(!empty($keyword)){
				$stmt->bind_param('s', $bind_keyword);
			}
			// Execute query
			$stmt->execute();
			// store result 
			$stmt->store_result();
			$stmt->bind_result($data['id'], 
					$data['radio_name'], 
					$data['radio_image'], 
					$data['category_name'],
					$data['radio_url']
					);
			// get total records
			$total_records = $stmt->num_rows;
		}
			
		// check page parameter
		if(isset($_GET['page'])){
			$page = $_GET['page'];
		}else{
			$page = 1;
		}
						
		// number of data that will be display per page		
		$offset = 10;
						
		//lets calculate the LIMIT for SQL, and save it $from
		if ($page){
			$from 	= ($page * $offset) - $offset;
		}else{
			//if nothing was given in page request, lets load the first page
			$from = 0;	
		}	
		
		if(empty($keyword)){
			$sql_query = "SELECT id, radio_name, radio_image, category_name, radio_url 
					FROM tbl_radio m, tbl_category c
					WHERE m.category_id = c.cid  
					ORDER BY m.id DESC LIMIT ?, ?";
		}else{
			$sql_query = "SELECT id, radio_name, radio_image, category_name, radio_url 
					FROM tbl_radio m, tbl_category c
					WHERE m.category_id = c.cid AND radio_name LIKE ? 
					ORDER BY m.id DESC LIMIT ?, ?";
		}
		
		$stmt_paging = $connect->stmt_init();
		if($stmt_paging ->prepare($sql_query)) {
			// Bind your variables to replace the ?s
			if(empty($keyword)){
				$stmt_paging ->bind_param('ss', $from, $offset);
			}else{
				$stmt_paging ->bind_param('sss', $bind_keyword, $from, $offset);
			}
			// Execute query
			$stmt_paging ->execute();
			// store result 
			$stmt_paging ->store_result();
			$stmt_paging->bind_result($data['id'], 
					$data['radio_name'], 
					$data['radio_image'], 
					$data['category_name'],
					$data['radio_url']
					);
			// for paging purpose
			$total_records_paging = $total_records; 
		}

		// if no data on database show "No Reservation is Available"
		if($total_records_paging == 0){
	
	?>
	<!-- START CONTENT -->
    <section id="content">

        <!--breadcrumbs start-->
        <div id="breadcrumbs-wrapper" class=" grey lighten-3">
          	<div class="container">
            	<div class="row">
              		<div class="col s12 m12 l12">
               			<h5 class="breadcrumbs-title">Radio List</h5>
		                <ol class="breadcrumb">
		                  <li><a href="dashboard.php">Dashboard</a>
		                  </li>
		                  <li><a href="#" class="active">Manage Radio</a>
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
				<div class="col s12 m12 l9">
				    <div class="row">
				        <form method="get" class="col s12">
				            <div class="row">
								<div class="input-field col s12">
				                    <h5>Radio Station is not Available</h5>
				                </div>								
				                <div class="input-field col s12">
				                    <a href="add-menu.php" class="btn waves-effect waves-light indigo">Add New Radio Station</a>
				                </div>
				             </div>
				        </form>
				    </div>
				</div>
			</div>
		</div>
	</section>

	<?php 
		// otherwise, show data
		}else{
			$row_number = $from + 1;
	?>

	<!-- START CONTENT -->
    <section id="content">

        <!--breadcrumbs start-->
        <div id="breadcrumbs-wrapper" class=" grey lighten-3">
          	<div class="container">
            	<div class="row">
              		<div class="col s12 m12 l12">
               			<h5 class="breadcrumbs-title">Radio List</h5>
		                <ol class="breadcrumb">
		                  <li><a href="dashboard.php">Dashboard</a>
		                  </li>
		                  <li><a href="#" class="active">Manage Radio</a>
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
				<div class="col s12 m12 l9">
				    <div class="row">
				        <form method="get" class="col s12">
				            <div class="row">
								<div class="input-field col s3">
				                    <a href="add-menu.php" class="btn waves-effect waves-light indigo">Add New Radio Station</a>
				                </div>
				                <div class="input-field col s5">
				                    <input type="text" class="validate" name="keyword">
				                    <label for="first_name">Search by Name :</label>
				                </div>
				                <div class="input-field col s4">
				                	<button type="submit" name="btnSearch" class="btn-floating btn-large waves-effect waves-light"><i class="mdi-action-search"></i></button>
				                </div>
				             </div>
				        </form>
				    </div>
				</div>
            
				<div class="row">
		            <div class="col s12 m12 l12">
		              	<div class="card-panel">
		                	<div class="row">
		                  		<div class="row">
		                    		<div class="input-field col s12">
	
										<table class='hoverable bordered'>
										<thead>
											<tr>
												<th>Radio Name</th>
												<th>Image</th>
												<th>Radio Stream Url</th>
												<th>Category</th>
												<th>Action</th>
											</tr>
										</thead>

											<?php 
												while ($stmt_paging->fetch()){ ?>
												<tbody>
													<tr>
														<td><?php echo $data['radio_name'];?></td>
														<td><img src="upload/<?php echo $data['radio_image']; ?>" width="90" height="90"/></td>
														<td><?php echo $data['radio_url'];?></td>
														<td><?php echo $data['category_name'];?></td>
														<td>					
															<a href="edit-menu.php?id=<?php echo $data['id'];?>">
															<i class="mdi-editor-mode-edit"></i>
															</a>
															<a href="delete-menu.php?id=<?php echo $data['id'];?>">
															<i class="mdi-action-delete"></i>
															</a>															
														</td>
													</tr>
												</tbody>
													<?php 
													} 
												}
											?>
										</table>

										<h4><?php $function->doPages($offset, 'radio.php', '', $total_records, $keyword); ?></h4>

		                    		</div>
		                  		</div>
		                	</div>
		              	</div>
		            </div>
		        </div>
        	</div>
        </div>

	</section>	

<?php 
	$stmt->close();
	include_once('includes/close_database.php'); ?>
					
				