<?php
/**
* call the python script and and format the output to return a 2D array like this
* [[article1, nbviews], [article2, nb views],...] sorted by rank
* Return null if script doesn't return expected Json
*/
function getData($beginDate, $endDate, $nbTops){

    $JSON =  shell_exec("python3 request.py 1 " . escapeshellarg($startDate) . " " . escapeshellarg($endDate) . " " . escapeshellarg($nbTops));
    //$JSON = "[{'rank': 1, 'article': 'Main_Page', 'views': 35410749}, {'rank': 2, 'article': 'Special:Search', 'views': 3584221}, {'rank': 3, 'article': 'Special:CreateAccount', 'views': 691997}, {'rank': 4, 'article': 'XHamster', 'views': 540782}, {'rank': 5, 'article': 'Special:RecentChangesLinked/Markem-Imaje', 'views': 534655}]";

    $JSON = str_replace("'", '"', $JSON);
    $JSON = json_decode($JSON);
    $formatedData = array();
    foreach ($JSON as  $article) {
        array_push($formatedData, [$article->article,$article->views]);
    }
    return $formatedData;
}

/**
* Take the python output and generate a table with article rank (using formatOutput)
**/
function generateTable($data){
     $table = <<<EOF
     <table class="table table-striped">
       <thead class="thead-light">
       <tr>
         <th scope="col">Rank</th>
         <th scope="col">Topic</th>
         <th scope="col">Number of views</th>
       </tr>
       </thead>
       <tbody>
EOF;

     $i = 1;
     foreach ($data as $elt) {
         $table .= <<< EOF
         <tr>
           <th scope="row">$i</th>
           <td>$elt[0]</td>
           <td>$elt[1]</td>
         </tr>
EOF;
     $i+=1;
     }

     $table .= <<<EOF
       </tbody>
     </table>
EOF;

    return $table;
}

function isDateValid($date){
    $pattern = "/[1-9][0-9]{3}-(0[1-9]|1[0-2])-([012][1-9]|3[01])/";
    return preg_match($pattern, $date);
}

function compareDates($a, $b) {
    $a = intval(str_replace("-","",$a));
    $b = intval(str_replace("-","",$b));
    return ($a == $b ? 0 : ($a < $b ? -1 : 1));
}


echo  <<<EOF
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Wikipedia Analyzer: Top of the day</title>
        <link rel="stylesheet" href="resources/css/bootstrap.min.css">
        <script src="resources/js/bootstrap.min.js" charset="utf-8"></script>
    </head>
    <body>
    <a href="index.html" class="btn btn-primary btn-lg active" role="button" aria-pressed="true">Home</a>
    <div class="container">
EOF;

if(!(isset($_POST["beginDateTop"]) && !empty($_POST["beginDateTop"]) && isDateValid($_POST["beginDateTop"]))) {
    echo "Please specify a valid starting date <a href='index.html'>Back to home</a></body></html>";
    return;
}

if(!(isset($_POST["endDateTop"]) && !empty($_POST["endDateTop"]) && isDateValid($_POST["endDateTop"]))) {
    echo "Please specify a valid ending date <a href='index.html'>Back to home</a></body></html>";
    return;
}

if(compareDates($_POST["beginDateTop"], $_POST["endDateTop"]) != -1){
    echo "The begging date have to be before the ending date.<a href='index.html'>Back to home</a></body></html>";
    return;
}

if(!(isset($_POST["nbTops"]) && !empty($_POST["nbTops"]) &&     ($_POST["nbTops"]>=1&&$_POST["nbTops"]<=50))){
    echo "Please enter a valid number of top (between 1 and 100).<a href='index.html'>Back to home</a></body></html>";
    return;
}

echo "<h1 class='display-4 text-center'>Top ".htmlspecialchars($_POST["nbTops"])." articles between " . htmlspecialchars($_POST["beginDateTop"]) ." and " .htmlspecialchars($_POST["endDateTop"])."</h1>";



$data = getData($_POST["beginDateTop"],$_POST["endDateTop"],$_POST["nbTops"]);

if($data == null) {
    echo "Error: No data retrieved</div></body></html>";
    return;
}

echo generateTable($data);

echo "</div></body></html>";
