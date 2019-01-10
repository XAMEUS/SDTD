<?php

echo  <<<EOF
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Wikipedia Analyzer: Article history</title>
        <link rel="stylesheet" href="resources/css/bootstrap.min.css">
        <script src="resources/js/bootstrap.min.js" charset="utf-8"></script>
    </head>
    <body>
    <a href="index.html" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Home</a>
    <div class="container">
EOF;

if(!(isset($_POST["name"]) && !empty($_POST["name"]))) {
    echo "<h1>Error</h1> Please specify the name of the article you want to see <a href='index.html'>Back to home</a></body></html>";
    return;
}

$name = $_POST["name"];
echo "<h1 class='display-4 text-center'>History of ".htmlspecialchars($name)."</h1>";

#TODO call python script
#TODO format data from python
#TODO if no data  => error message
#TODO display data in a graph

echo "</div></body></html>";

 ?>
