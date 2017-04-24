<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=no">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="msapplication-tap-highlight" content="no">
    <meta name="description"
          content="Materialize is a Material Design Admin Template,It's modern, responsive and based on Material Design by Google. ">
    <meta name="keywords"
          content="materialize, admin template, dashboard template, flat admin template, responsive admin template,">
    <title>Your Radio App</title>

    <!-- Favicons-->
    <link rel="icon" href="assets/images/favicon/favicon-32x32.png" sizes="32x32">
    <!-- Favicons-->
    <link rel="apple-touch-icon-precomposed" href="assets/images/favicon/apple-touch-icon-152x152.png">
    <!-- For iPhone -->
    <meta name="msapplication-TileColor" content="#00bcd4">
    <meta name="msapplication-TileImage" content="assets/images/favicon/mstile-144x144.png">
    <!-- For Windows Phone -->


    <!-- CORE CSS-->

    <link href="assets/css/materialize.css" type="text/css" rel="stylesheet" media="screen,projection">
    <link href="assets/css/style.css" type="text/css" rel="stylesheet" media="screen,projection">


    <!-- INCLUDED PLUGIN CSS ON THIS PAGE -->
    <link href="assets/css/prism.css" type="text/css" rel="stylesheet" media="screen,projection">
    <link href="assets/js/plugins/perfect-scrollbar/perfect-scrollbar.css" type="text/css" rel="stylesheet"
          media="screen,projection">
    <link href="assets/js/plugins/chartist-js/chartist.min.css" type="text/css" rel="stylesheet"
          media="screen,projection">
</head>

<body>

<!-- //////////////////////////////////////////////////////////////////////////// -->

<!-- START HEADER -->
<header id="header" class="page-topbar">
    <!-- start header nav-->
    <div class="navbar-fixed">
        <nav class="cyan">
            <div class="nav-wrapper">
                <h1 class="logo-wrapper"><a href="dashboard.php" class="brand-logo darken-1"><img
                        src="assets/images/materialize-logo.png" alt="materialize logo"></a> <span
                        class="logo-text">Your Radio App</span></h1>
                <ul class="right hide-on-med-and-down">
                    <li><a href="#push-notification"
                           class="waves-effect waves-block waves-light modal-trigger"><i
                            class="mdi-social-notifications"></i></a>
                    </li>
                    <!-- Dropdown Trigger -->
                    <li><a class="dropdown-button" href="javascript:void(0);"
                           data-activates="dropdown1"><i class="mdi-navigation-arrow-drop-down"></i></a>
                    </li>

                    <!-- Dropdown Structure -->
                    <ul id="dropdown1" class="dropdown-content">
                        <li><a href="admin.php">Settings</a></li>
                        <li><a href="logout.php">Logout</a></li>
                    </ul>

                </ul>
            </div>
        </nav>
    </div>
    <!-- end header nav-->
</header>
<!-- END HEADER -->

<!-- START MAIN -->
<div id="main">
    <!-- START WRAPPER -->
    <div class="wrapper">

        <!-- START LEFT SIDEBAR NAV-->
        <aside id="left-sidebar-nav">
            <ul id="slide-out" class="side-nav fixed leftside-navigation">
                <li class="user-details cyan darken-2">
                    <div class="row">
                        <div class="col col s4 m4 l4">
                            <center>
                                <img src="assets/images/ic_launcher.png" width="100px" height="100px">
                            </center>
                        </div>
                    </div>
                </li>
                <li class="bold">
                    <a href="dashboard.php" class="waves-effect waves-cyan">
                        <i class="mdi-action-dashboard"></i>Dashboard
                    </a>
                </li>

                <li class="bold">
                    <a href="category.php" class="waves-effect waves-cyan">
                        <i class="mdi-av-queue-music"></i>Category
                    </a>
                </li>

                <li class="bold">
                    <a href="radio.php" class="waves-effect waves-cyan">
                        <i class="mdi-av-radio"></i>Radio Station
                    </a>
                </li>                

                <li class="bold">
                    <a href="admin.php" class="waves-effect waves-cyan">
                        <i class="mdi-action-settings"></i>Settings
                    </a>
                </li>

                <li class="bold">
                    <a href="logout.php" class="waves-effect waves-cyan">
                        <i class="mdi-hardware-keyboard-tab"></i>Logout
                    </a>
                </li>

            </ul>
            <a href="#" data-activates="slide-out"
               class="sidebar-collapse btn-floating btn-medium waves-effect waves-light hide-on-large-only darken-2"><i
                    class="mdi-navigation-menu"></i></a>
        </aside>
        <!-- END LEFT SIDEBAR NAV-->

        <!-- //////////////////////////////////////////////////////////////////////////// -->
                <div id="push-notification" class="modal">
                  <div class="modal-content">
                  <h4>Information</h4>
                  <hr>
                    <p>we use a third party service for push notifications. for Android Studio, we use Firebase services which recommended by Google, because Firebase services don't support for Eclipse, alternatively we integrate with OneSignal.
                    </p>
                    <p>Please select the options below, then you will be directed to web site which related with push notification services</p>
                  </div>
                  <div class="modal-footer">
                    <a href="https://console.firebase.google.com" target="_blank" class="waves-effect waves-red btn-flat modal-action modal-close">Android Studio</a>
                    <a href="https://onesignal.com/" target="_blank" class="waves-effect waves-green btn-flat modal-action modal-close">Eclipse</a>
                  </div>
                </div>