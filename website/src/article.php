<?php

#call the python script and format data in this format with x the date and y the number of view
function getData(){
    //TODO getData
    return array(
	array("date" => "1996-12-13", "val" => 71),
	array("date" => "1996-12-14", "val" => 55),
	array("date" => "1996-12-15", "val" => 50),
	array("date" => "1996-12-16", "val" => 65),
	array("date" => "1996-12-17", "val" => 95),
	array("date" => "1996-12-18", "val" => 68),
	array("date" => "1996-12-19", "val" => 28),
	array("date" => "1996-12-20", "val" => 34),
	array("date" => "1996-12-21", "val" => 14),
    );
}

function displayGraph($data){
    $data = json_encode($data, JSON_NUMERIC_CHECK);
    $js = <<< JS
    window.onload = function () {
        var svg = d3.select("svg")
              .attr("width", 1024)
              .attr("height", 480)

        var margin = {left:30, right:30, top: 10, bottom: 20}
        var width = svg.attr("width") - margin.left - margin.right;
        var height = svg.attr("height") - margin.bottom - margin.top;
        var data = $data;

        var x = d3.scaleTime().rangeRound([0, width]);
        var y = d3.scaleLinear().rangeRound([height, 0]);

        var xFormat = "%d-%m-%Y";;
        var parseTime = d3.timeParse("%Y-%m-%d");

        x.domain(d3.extent(data, function(d) { return parseTime(d.date); }));
      	y.domain([0,d3.max(data, function(d) {return d3.max([d.val]);})]);

        var a = function(d) {return d.val};

        var g = svg.append("g").attr("transform",
                        "translate(" + margin.left + "," + margin.top + ")");

        var line = d3.line().x(function(d) { return x(parseTime(d.date)); })
                            .y(function(d) { return y(d.val); })

        g.append("path").datum(data).attr("d", line).attr("stroke", "blue").attr("stroke-width", 2).attr("fill", "none");

        g.append("g").attr("transform", "translate(0," + height + ")")
                     .call(d3.axisBottom(x).tickFormat(d3.timeFormat(xFormat)));
        g.append("g").call(d3.axisLeft(y));



    }
JS;
    echo "<svg></svg><script>$js</script>";
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
        <script src="https://d3js.org/d3.v4.min.js"></script>
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
