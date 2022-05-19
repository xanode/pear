//Data
var myData = {
    "nodes": [
        {
          "id": "id1",
          "name": "name1",
          "val": 1
        },
        {
          "id": "id2",
          "name": "name2",
          "val": 10
        }
    ],
    "links": [
        {
            "source": "id1",
            "target": "id2"
        }
    ]
}


//size
const main_panel = document.getElementById('main_panel');
var height = main_panel.clientHeight*(2/3);
var width = main_panel.clientWidth*(2/3);

//3D graph
const div_3d = document.getElementById('forced-graph-3d');
var myGraph_3d = ForceGraph3D()(div_3d)
.width(width)
.height(height)
.graphData(myData);

//2D graph
const div_2d = document.getElementById('forced-graph-2d');
var myGraph_2d = ForceGraph()(div_2d)
.width(width)
.height(height)
.graphData(myData);


//load table
const labels_line = document.getElementById("labels");
for (let i = 0; i < myData["nodes"].length; i++) {
  const node = myData["nodes"][i];

  
  const link = myData["links"][i];

  var new_line = document.createElement("tr");
  var row_id = document.createElement("td");
  var cellText = document.createTextNode(node["id"]);
  row_id.appendChild(cellText);
  var row_name = document.createElement("td");
  var cellText = document.createTextNode(node["name"]);
  row_name.appendChild(cellText);
  var row_val = document.createElement("td");
  var cellText = document.createTextNode(node["val"]);
  row_val.appendChild(cellText);
  var row_target = document.createElement("td");
  var cellText = document.createTextNode(link["target"]);
  row_target.appendChild(cellText);
  
  new_line.appendChild(row_id);
  labels_line.after(new_line);
};


