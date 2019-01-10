<?php

#call the python script and format data in this format with x the date and y the number of view
function getData(){
    //TODO getData
    return array(
	array("x" => 19961213, "y" => 71),
	array("x" => 19961214, "y" => 55),
	array("x" => 19961215, "y" => 50),
	array("x" => 19961216, "y" => 65),
	array("x" => 19961217, "y" => 95),
	array("x" => 19961218, "y" => 68),
	array("x" => 19961219, "y" => 28),
	array("x" => 19961220, "y" => 34),
	array("x" => 19961221, "y" => 14),
    );
}

function displayGraph($data){
    $data = json_encode($data, JSON_NUMERIC_CHECK);
    $js = <<< JS
    window.onload = function () {
                var chart = new CanvasJS.Chart("chartContainer", {
                theme: "light1",
                zoomEnabled: true,
                animationEnabled: true,
                title: {
                    text: "Number of views"
                },
                data: [
                {
                    type: "line",
                    dataPoints: $data
                }
                ]
            });
            chart.render();
        }
JS;
    echo "<div id='chartContainer'></div><script>$js</script>";
}


function isDateValid($date){
    $pattern = "/[1-9][0-9]{3}-(0[1-9]|1[0-2])-([012][1-9]|3[01])/";
    return preg_match($pattern, $date);
}

/**
*  Compare 2 dates
*  return -1 if date1<date2, 0 if equal and 1 if date1>date2
*/
function compareDates($date1, $date2){
    $date1 = explode("-", $date1);
    $date2 = explode("-", $date2);
    for( $i=0; $i<3;$i++){
        if($date1[$i]>$date2[$i])
            return 1;
        if($date1[$i]<$date2[$i])
            return -1;
    }
    return 0;
}

echo  <<<EOF
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Wikipedia Analyzer: Article history</title>
        <link rel="stylesheet" href="resources/css/bootstrap.min.css">
        <script src="resources/js/canvasjs.min.js"></script>
    </head>
    <body>
    <a href="index.html" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Home</a>
    <div class="container">
EOF;

if(!(isset($_POST["name"]) && !empty($_POST["name"]))) {
    echo "<h1 class='display-4 text-center'>Error</h1> Please specify the name of the article you want to see. <a href='index.html'>Back to home</a></body></html>";
    return;
}

$name = $_POST["name"];
echo "<h1 class='display-4 text-center'>History of ".htmlspecialchars($name)."</h1>";


if(!(isset($_POST["startDate"]) && !empty($_POST["startDate"]) && isDateValid($_POST["startDate"]))) {
    echo "Please specify a valid starting date <a href='index.html'>Back to home</a></body></html>";
    return;
}

if(!(isset($_POST["endDate"]) && !empty($_POST["endDate"]) && isDateValid($_POST["endDate"]))) {
    echo "Please specify a valid ending date <a href='index.html'>Back to home</a></body></html>";
    return;
}

$startDate = $_POST["startDate"];
$endDate = $_POST["endDate"];

if(compareDates($startDate, $endDate) != -1){
    echo "The begging date have to be before the ending date.<a href='index.html'>Back to home</a></body></html>";
    return;
}

$data = getData($name, $startDate, $endDate);
if($data == null) {
    echo "Error: No data retrieved</div></body></html>";
    return;
}

displayGraph($data);
echo "</div></body></html>";

 ?>
