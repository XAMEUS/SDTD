<?php
/**
* Take the output of the python script to format it in a 2D array like this
* [[article1, nbviews], [article2, nb views],...] sorted by rank
*
*/
function formatOutput($output){
    //TODO Implement this function to return a correct array using python's data
    return [
        ["Article1", 9520525],
        ["Article2", 5415648],
        ["Article3", 2654666]];
}

/**
* Take the python output and generate a table with article rank (using formatOutput)
**/
function generateTable($output){
     $table = <<<EOF
     <table class="table table-striped">
       <thead class="thead-light">
       <tr>
         <th scope="col">Rank</th>
         <th scope="col">Article</th>
         <th scope="col">Number of views</th>
       </tr>
       </thead>
       <tbody>
EOF;

     $i = 1;
     foreach (formatOutput($output) as $elt) {
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


echo  <<<EOF
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Wikipedia Analyzer</title>
        <link rel="stylesheet" href="resources/css/bootstrap.min.css">
        <script src="resources/js/bootstrap.min.js" charset="utf-8"></script>
    </head>
    <body>
    <div class="container">
EOF;


if(!(isset($_POST["date"]) && !empty($_POST["date"]))) {
    echo "<h1>Error</h1> Please specify a date <a href='index.html'>Back to home</a></body></html>";
    return;
}

$analyzeDate = $_POST["date"];
echo "<h1 class='display-4 text-center'>Top articles of $analyzeDate</h1>";


#TODO Recuperer donnes de python
$command = escapeshellcmd("python3 analyze.py $analyzeDate");
$output = shell_exec($command);


if($output == null) {
    echo "Error during command: python3 analyze.py $analyzeDate";
    return;
}

echo generateTable($output);

echo "</div></body></html>";
