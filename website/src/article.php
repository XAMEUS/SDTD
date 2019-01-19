<?php

/**
* call the python script to get data about a topic and format data in this format:
* [["date" : "1996-12-13", "val": 10],["date" : "1996-12-13", "val": 15]]
* Do not forget to encode with json_encode($data, JSON_NUMERIC_CHECK);
*/
function getData($name, $startDate, $endDate){

    $jsonData =  shell_exec("python3 request.py 0 " . escapeshellarg($startDate) . " " . escapeshellarg($endDate) . " " . escapeshellarg($name));
    //$jsonData = "{'totalViews': 1256686, 'views': [{'date': '2019-01-09', 'views': '99596'}, {'date': '2019-01-02', 'views': '148810'},{'date': '2019-01-01', 'views': '140019'},  {'date': '2019-01-03', 'views': '127657'}, {'date': '2019-01-04', 'views': '120240'}, {'date': '2019-01-05', 'views': '133910'}, {'date': '2019-01-06', 'views': '159027'}, {'date': '2019-01-07', 'views': '133245'}, {'date': '2019-01-08', 'views': '105322'},  {'date': '2019-01-10', 'views': '88860'}]}";
    $data = formatData($jsonData);
    return json_encode($data, JSON_NUMERIC_CHECK);

}

function compareDates($a, $b) {
    $a = intval(str_replace("-","",$a));
    $b = intval(str_replace("-","",$b));
    return ($a == $b ? 0 : ($a < $b ? -1 : 1));

}

function formatData($json){
    $json = str_replace("'", '"', $json);
    $json = str_replace("views", 'val', $json);
    $data = json_decode($json);
    $data = $data->val;
    $dates = array();
    foreach ($data as $key => $row)
    {
        array_push($dates, $row->date);
    }
    array_multisort($dates, SORT_ASC, $data);

    return $data;
}

function displayGraph($data){
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
    //return preg_match($pattern, $date);
    return true;
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
