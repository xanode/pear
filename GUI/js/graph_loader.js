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
nodes = myData["nodes"];
console.log(nodes);
nodes.forEach(node => {
  const text = document.getElementById("nodes");
  text.innerHTML = text.textContent + " "+node["name"];
});
