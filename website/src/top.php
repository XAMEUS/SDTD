<?php
/**
* call the python script and and format the output to return a 2D array like this
* [[article1, nbviews], [article2, nb views],...] sorted by rank
*
*/
function getData($analyzeDate){
    //TODO Call python to get data about the top articles of the given date
    // $command = escapeshellcmd("python3 analyze.py $analyzeDate");
    // $output = shell_exec($command);
    return [
        ["Topic1", 9520525],
        ["Topic2", 5415648],
        ["Topic3", 2654666]];
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


if(!(isset($_POST["date"]) && !empty($_POST["date"] && isDateValid($_POST["date"])))) {
    echo "<h1>Error</h1> Please specify a valid date <a href='index.html'>Back to home</a></body></html>";
    return;
}

$date = $_POST["date"];
echo "<h1 class='display-4 text-center'>Top articles of $date</h1>";

$data = getData($date);

if($data == null) {
    echo "Error: No data retrieved";
    return;
}

echo generateTable($data);

echo "</div></body></html>";
