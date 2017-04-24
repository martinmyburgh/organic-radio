<?php
  include_once('includes/connect_database.php'); 
  include_once('functions.php'); 
?>

<?php

  //Total category count
  $sql_category = "SELECT COUNT(*) as num FROM tbl_category";
  $total_category = mysqli_query($connect, $sql_category);
  $total_category = mysqli_fetch_array($total_category);
  $total_category = $total_category['num'];

  //Total radio count
  $sql_radio = "SELECT COUNT(*) as num FROM tbl_radio";
  $total_radio = mysqli_query($connect, $sql_radio);
  $total_radio = mysqli_fetch_array($total_radio);
  $total_radio = $total_radio['num'];

?>
        <!--breadcrumbs start-->
        <div id="breadcrumbs-wrapper" class=" grey lighten-3">
          <div class="container">
            <div class="row">
              <div class="col s12 m12 l12">
                <h5 class="breadcrumbs-title">Dashboard</h5>
                <ol class="breadcrumb">
                  <li><a href="dashboard.php">Dashboard</a>
                  </li>
                  <li><a href="#" class="active">Home</a>
                </ol>
              </div>
            </div>
          </div>
        </div>
        <!--breadcrumbs end-->

        <!--start container-->
        <div class="container">
            <div class="section">

                        <!--card stats start-->
            <div id="card-stats" class="seaction">
              <div class="row">
                            <div class="col s12 m6 l3">
                            <a href="category.php">
                                <div class="card">
                                    <div class="card-content deep-purple white-text">
                                        <p class="card-stats-title"><i class="mdi-social-group-add"></i> CATEGORY</p>
                                        <h4 class="card-stats-number"><?php echo $total_category;?></h4>
                                        <p class="card-stats-compare"><span class="blue-grey-text text-lighten-5">Total Category</span>
                                        </p>
                                    </div>
                                    <div class="card-action  blue-grey darken-2">
                                        <div id="clients-bar"></div>
                                    </div>
                                </div>
                            </a>
                            </div>

                            <div class="col s12 m6 l3">
                            <a href="radio.php">
                                <div class="card">
                                    <div class="card-content deep-purple white-text">
                                        <p class="card-stats-title"><i class="mdi-social-group-add"></i> RADIO LIST</p>
                                        <h4 class="card-stats-number"><?php echo $total_radio;?></h4>
                                        <p class="card-stats-compare"><span class="blue-grey-text text-lighten-5">Total Radio</span>
                                        </p>
                                    </div>
                                    <div class="card-action  blue-grey darken-2">
                                        <div id="clients-bar"></div>
                                    </div>
                                </div>
                            </a>
                            </div>

                            <div class="col s12 m6 l3">
                            <a href="#push-notification" class="modal-trigger">
                                <div class="card">
                                    <div class="card-content deep-purple white-text">
                                        <p class="card-stats-title"><i class="mdi-social-group-add"></i> PUSH NOTIFICATION</p>
                                        <h4 class="card-stats-number"><i class="mdi-social-notifications"></i></h4>
                                        <p class="card-stats-compare"><span class="blue-grey-text text-lighten-5">Go to Firebase Console</span>
                                        </p>
                                    </div>
                                    <div class="card-action  blue-grey darken-2">
                                        <div id="clients-bar"></div>
                                    </div>
                                </div>
                            </a>
                            </div>                              

                            <div class="col s12 m6 l3">
                            <a href="admin.php">
                                <div class="card">
                                    <div class="card-content deep-purple white-text">
                                        <p class="card-stats-title"><i class="mdi-social-group-add"></i> SETTINGS</p>
                                        <h4 class="card-stats-number"><i class="mdi-action-settings"></i></h4>
                                        <p class="card-stats-compare"><span class="blue-grey-text text-lighten-5">Manage Settings</span>
                                        </p>
                                    </div>
                                    <div class="card-action  blue-grey darken-2">
                                        <div id="clients-bar"></div>
                                    </div>
                                </div>
                            </a>
                            </div>                           
                           
                        </div>
            </div>
            <!--card stats end-->
    </div>
</div> 

<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<?php include_once('includes/close_database.php'); ?>