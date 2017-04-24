-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Aug 06, 2016 at 01:32 AM
-- Server version: 10.1.9-MariaDB
-- PHP Version: 5.5.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `your_radio_app`
--

-- --------------------------------------------------------

--
-- Table structure for table `tbl_category`
--

CREATE TABLE `tbl_category` (
  `cid` int(11) NOT NULL,
  `category_name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `category_image` varchar(255) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tbl_category`
--

INSERT INTO `tbl_category` (`cid`, `category_name`, `category_image`) VALUES
(4, 'Miscellaneous', '1586-2015-11-15.png'),
(5, 'Culture', '6563-2015-11-15.png'),
(7, 'Education', '8649-2015-11-15.png'),
(8, 'Newscast', '2215-2015-11-15.png'),
(9, 'Entertainment', '5688-2015-11-15.png');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_radio`
--

CREATE TABLE `tbl_radio` (
  `id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `radio_name` varchar(255) CHARACTER SET utf8 NOT NULL,
  `radio_image` varchar(255) CHARACTER SET utf8 NOT NULL,
  `radio_url` varchar(255) CHARACTER SET utf8 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tbl_radio`
--

INSERT INTO `tbl_radio` (`id`, `category_id`, `radio_name`, `radio_image`, `radio_url`) VALUES
(22, 9, 'Maestro Bandung FM', '8841-2015-11-15.jpg', 'http://192.99.170.8:5756/'),
(24, 4, 'Radio Jaca', '4548-2015-11-15.jpg', 'http://136.243.20.131:8520'),
(25, 5, 'Radio Rama', '3757-2015-11-15.jpg', 'http://rama-fm.simaya.net.id:8800/'),
(26, 4, 'Radio Niagara', '5962-2015-11-15.jpg', 'http://206.190.144.154:8012/'),
(29, 7, 'Radio Rodja', '1593-2015-11-15.jpg', 'http://live.radiorodja.com/'),
(30, 8, 'Radio Zelengrad', '8896-2015-11-15.jpg', 'http://108.166.161.206:8750/'),
(31, 9, 'Suara Madu FM', '0253-2015-11-15.jpg', 'http://206.190.144.154:8012/'),
(33, 9, 'V Radio FM Jakarta', '5028-2015-11-15.jpg', 'http://202.147.199.100:8000/');

-- --------------------------------------------------------

--
-- Table structure for table `tbl_user`
--

CREATE TABLE `tbl_user` (
  `ID` int(11) NOT NULL,
  `Username` varchar(15) NOT NULL,
  `Password` text NOT NULL,
  `Email` varchar(100) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Dumping data for table `tbl_user`
--

INSERT INTO `tbl_user` (`ID`, `Username`, `Password`, `Email`) VALUES
(1, 'admin', 'd82494f05d6917ba02f7aaa29689ccb444bb73f20380876cb05d1f37537b7892', 'developer.solodroid@gmail.com');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tbl_category`
--
ALTER TABLE `tbl_category`
  ADD PRIMARY KEY (`cid`);

--
-- Indexes for table `tbl_radio`
--
ALTER TABLE `tbl_radio`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tbl_user`
--
ALTER TABLE `tbl_user`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tbl_category`
--
ALTER TABLE `tbl_category`
  MODIFY `cid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
--
-- AUTO_INCREMENT for table `tbl_radio`
--
ALTER TABLE `tbl_radio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;
--
-- AUTO_INCREMENT for table `tbl_user`
--
ALTER TABLE `tbl_user`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
