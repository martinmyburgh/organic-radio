<?php
	include_once('includes/connect_database.php'); 
	include('includes/variables.php'); 
?>

	<?php 
			$username = $_SESSION['user'];
			$sql_query = "SELECT Password, Email 
					FROM tbl_user 
					WHERE Username = ?";
			
			// create array variable to store previous data
			$data = array();
			
			$stmt = $connect->stmt_init();
			if($stmt->prepare($sql_query)) {
				// Bind your variables to replace the ?s
				$stmt->bind_param('s', $username);			
				// Execute query
				$stmt->execute();
				// store result 
				$stmt->store_result();
				$stmt->bind_result($data['Password'], $data['Email']);
				$stmt->fetch();
				$stmt->close();
			}
			
			$previous_password = $data['Password'];
			$previous_email = $data['Email'];
			
			if(isset($_POST['btnChange'])){
				$email = $_POST['email'];
				$old_password = hash('sha256',$username.$_POST['old_password']);
				$new_password = hash('sha256',$username.$_POST['new_password']);
				$confirm_password = hash('sha256',$username.$_POST['confirm_password']);
				
				// create array variable to handle error
				$error = array();
				
				// check password
				if(!empty($_POST['old_password']) || !empty($_POST['new_password']) || !empty($_POST['confirm_password'])){
					if(!empty($_POST['old_password'])){
						if($old_password == $previous_password){
							if(!empty($_POST['new_password']) || !empty($_POST['confirm_password'])){
								if($new_password == $confirm_password){
									// update password in user table
									$sql_query = "UPDATE tbl_user 
											SET Password = ?
											WHERE Username = ?";
									
									$stmt = $connect->stmt_init();
									if($stmt->prepare($sql_query)) {	
										// Bind your variables to replace the ?s
										$stmt->bind_param('ss', 
													$new_password, 
													$username);
										// Execute query
										$stmt->execute();
										// store result 
										$update_result = $stmt->store_result();
										$stmt->close();
									}
								}else{
									$error['confirm_password'] = " <span class='label label-danger'>New password don't match!</span>";
								}
							}else{
								$error['confirm_password'] = " <span class='label label-danger'>Please insert your new password and re new password!</span>";
							}
						}else{
							$error['old_password'] = " <span class='label label-danger'>Your old password is wrong!</span>";
						}
					}else{
						$error['old_password'] = " <span class='label label-danger'>Please insert your old password!</span>";
					}
				}
				
				if(empty($email)){
					$error['email'] = " <span class='label label-danger'>Please insert your email!</span>";
				}else{
					$valid_mail = "/^[_\.0-9a-zA-Z-]+@([0-9a-zA-Z][0-9a-zA-Z-]+\.)+[a-zA-Z]{2,6}$/i";
					if (!preg_match($valid_mail, $email)){
						$error['email'] = " <span class='label label-danger'>your email format false!</span>";
						$email = "";
					}else{
						// update password in user table
						$sql_query = "UPDATE tbl_user 
								SET Email = ?
								WHERE Username = ?";
						
						$stmt = $connect->stmt_init();
						if($stmt->prepare($sql_query)) {	
							// Bind your variables to replace the ?s
							$stmt->bind_param('ss', 
										$email, 
										$username);
							// Execute query
							$stmt->execute();
							// store result 
							$update_result = $stmt->store_result();
							$stmt->close();
						}
					}
				}
				
				// check update result
				if($update_result){
					//$to = $email;
					//$subject = $email_subject;
					//$message = $change_message;
					//$from = $admin_email;
					//$headers = 'From:' . $from;
					//mail($to,$subject,$message,$headers);
					$error['update_user'] = "<div class='card-panel teal lighten-2'>
											    <span class='white-text text-darken-2'>
												    User Data Successfully Changed
											    </span>
											</div>";
				}else{
					$error['update_user'] = "<div class='card-panel red darken-1'>
											    <span class='white-text text-darken-2'>
												    Added Failed
											    </span>
											</div>";
				}
			}		

			$sql_query = "SELECT Email FROM tbl_user WHERE Username = ?";
			
			$stmt = $connect->stmt_init();
			if($stmt->prepare($sql_query)) {
				// Bind your variables to replace the ?s
				$stmt->bind_param('s', $username);			
				// Execute query
				$stmt->execute();
				// store result 
				$stmt->store_result();
				$stmt->bind_result($previous_email);
				$stmt->fetch();
				$stmt->close();
			}		
	?>

	<!-- START CONTENT -->
    <section id="content">

        <!--breadcrumbs start-->
        <div id="breadcrumbs-wrapper" class=" grey lighten-3">
          	<div class="container">
            	<div class="row">
              		<div class="col s12 m12 l12">
               			<h5 class="breadcrumbs-title">Settings</h5>
		                <ol class="breadcrumb">
		                  <li><a href="dashboard.php">Dashboard</a>
		                  </li>
		                  <li><a href="#" class="active">Settings</a>
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
		                    				<?php echo isset($error['update_user']) ? $error['update_user'] : '';?>

											<div class="row">
						                      <div class="input-field col s12">
						                        <input type="text" name="username" id="username" value="<?php echo $username; ?>" disabled/>
						                        <label for="username">Username</label>
						                      </div>
						                    </div> 

											<div class="row">
						                      <div class="input-field col s12">
												<input type="text" name="email" id="email" value="<?php echo $previous_email; ?>" />
						                        <label for="email">Email</label><?php echo isset($error['email']) ? $error['email'] : '';?>
						                      </div>
						                    </div>
										
											<div class="row">
						                      <div class="input-field col s12">
						                        <input type="password" name="old_password" id="old_password" value="" />
						                        <label for="old_password">Old Password</label><?php echo isset($error['old_password']) ? $error['old_password'] : '';?>
						                      </div>
						                    </div>

											<div class="row">
						                      <div class="input-field col s12">
						                        <input type="password" name="new_password" id="new_password" value="" />
						                        <label for="new_password">New Password</label><?php echo isset($error['new_password']) ? $error['new_password'] : '';?>
						                      </div>
						                    </div>  

											<div class="row">
						                      <div class="input-field col s12">
						                        <input type="password" name="confirm_password" id="confirm_password" value="" />
						                        <label for="confirm_password">Re Type New Password</label><?php echo isset($error['confirm_password']) ? $error['confirm_password'] : '';?>
						                      </div>
						                    </div>	


											<button class="btn cyan waves-effect waves-light right" type="submit" name="btnChange">Update
						                        <i class="mdi-content-send left"></i>
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
