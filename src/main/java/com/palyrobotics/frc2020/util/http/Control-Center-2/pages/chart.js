import React from 'react';
import Link from 'next/link'
import NavBar from "../components/navbar"
import {Chart, Line} from 'react-chartjs-2';
import Chart2 from "../components/chart"
import fetchWithTimeout from "../components/fetch"

class Charts extends React.Component {
    getData = () => {
        fetch('http://10.0.8.2:4000')
        //fetch('http://localhost:4000')
            .then(res => res.json())
            .then((result) => {
                //apiResult = result.testObj;
                this.intervalID = setTimeout(this.getData.bind(this), 1);
                let newLogs = this.state.logs;
                //uncoment when testing with robot
                let graphData = result.graphData;
                /*let graphData = {
                    "one":  Math.random() * 100,
                    "two":  Math.random() * 100
                }*/
                for(const [table, value] of Object.entries(graphData)){
                    if(Object.keys(newLogs).includes(table)){
                        newLogs[table].push(value);
                    } else {
                        newLogs[table] = [value];
                    }
                }
                this.setState({
                    newInfoLoaded : true,
                    labels : [this.state.labels, this.state.curCount + 1].flat(),
                    curCount: this.state.curCount + 1
                });
            },
            (error) => {
                this.intervalID = setTimeout(this.getData.bind(this), 1);
            }
        )
    }

    constructor(props) {
        super(props);
        this.intervalID = null;
        this.state = {
            text : null,
            newInfoLoaded : false,
            logs: {},
            curCount: 0,
            labels: [],
            toDisplay: [],
            colors: {},
            merge: false,
            timedOut: false
        };
        this.handleCheck = this.handleCheck.bind(this);
    }

    componentDidMount() {
        this.getData();
    }

    componentWillUnmount() {
        clearTimeout(this.intervalID);
    }

    graph(graphs){
        console.log(this.state.merge);
        if(this.state.merge){
            let data = [];
            for(const table of graphs){
                let color = this.state.colors[table];
                data.push(
                    {
                      label: table,
                      fill: false,
                      lineTension: 0.1,
                      backgroundColor: color,
                      borderColor: color,
                      borderCapStyle: 'butt',
                      borderDash: [],
                      borderDashOffset: 0.0,
                      borderJoinStyle: 'miter',
                      pointBorderColor: color,
                      pointBackgroundColor: '#fff',
                      pointBorderWidth: 1,
                      pointHoverRadius: 5,
                      pointHoverBackgroundColor: color,
                      pointHoverBorderColor: color,
                      pointHoverBorderWidth: 2,
                      pointRadius: 1,
                      pointHitRadius: 10,
                      data: this.state.logs[table]
                    }
                )
            }
            let options = {
                pan: {
                    enabled: true,
                    mode: "xy"
                },
                zoom: {
                    enabled: true,
                    mode: "xy" // or 'x' for "drag" version
                },
                maintainAspectRatio: false 
            }
            return (<Chart2
              title="Chart 1"
              labels={this.state.labels}
              data={data}
              width={300}
              height={300}
            />)
        } else {
            let options = {
                pan: {
                    enabled: true,
                    mode: "xy"
                },
                zoom: {
                    enabled: true,
                    mode: "xy" // or 'x' for "drag" version
                },
                maintainAspectRatio: false 
            }
            let g = [];
            for(const table of graphs){
                let color = this.state.colors[table];
                let data = []
                data.push(
                    {
                      label: table,
                      fill: false,
                      lineTension: 0.1,
                      backgroundColor: color,
                      borderColor: color,
                      borderCapStyle: 'butt',
                      borderDash: [],
                      borderDashOffset: 0.0,
                      borderJoinStyle: 'miter',
                      pointBorderColor: color,
                      pointBackgroundColor: '#fff',
                      pointBorderWidth: 1,
                      pointHoverRadius: 5,
                      pointHoverBackgroundColor: color,
                      pointHoverBorderColor: color,
                      pointHoverBorderWidth: 2,
                      pointRadius: 1,
                      pointHitRadius: 10,
                      data: this.state.logs[table]
                    }
                )
                g.push(<Chart2
                   title="Chart 1"
                   labels={this.state.labels}
                   data={data}
                   width={300}
                   height={300}
                />
                )
            }

            return g; 
        }
    }

    handleCheck(event){
        const target = event.target;
        if(target.name === "merge"){
            this.setState({
                merge: target.checked
            });
            return
        }
        let display = this.state.toDisplay;
        let colors = ["rgba(200, 0, 0, 1)", "rgba(0, 200, 0, 1)", "rgba(0, 0, 200, 1)", "rgba(200, 100, 0, 1)", "rgba(200, 200, 0, 1)"]
        let curColors = this.state.colors;
        if(target.checked){
            display.push(target.name);
            if(Object.keys(curColors).includes(target.name)){
            }else{
                curColors[target.name] = colors[Math.floor(Math.random() * colors.length)]; 
            }
        } else {
            display.splice(display.indexOf(target.name), 1);
        }

        this.setState({
            toDisplay: display,
            colors: curColors
        })
    }

    render() {
        const { error, newInfoLoaded, text, logs, curCount, labels, timedOut} = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (timedOut) {
            return <div class="text-4xl font-bold text-red-600 text-center">Request Timed Out! Make sure you're connected to the robot wifi</div>
        } else if (newInfoLoaded === false) {
            return <div>Loading...</div>;
        } else {

            //return <div style={{width:"100%",height:500,overflow:"scroll",display: "flex", "flex-direction": "column-reverse", "background-color":"#333333", color:"white"}}>{logss}</div>;
            let inputs = [(<span>
                <input name="merge" type="checkbox" onChange={this.handleCheck}/>
                <a> merge </a>
            </span>)];
            let graphs = [];
            for(const table of Object.keys(this.state.logs)){
                inputs.push((<span>
                    <input name={table} type="checkbox" onChange={this.handleCheck}/>
                    <a> {table} </a>
                </span>));
                if(this.state.toDisplay.includes(table)){
                    graphs.push(table);
                }
            }
            return (
                <>
                    <NavBar></NavBar> 
                    <div class="font-mono grid place-items-center p-5">
                        {inputs}
                    </div>
                    <div>
                        {this.graph(graphs)}
                    </div>
                </>
            )
        }
    }

}
export default function Output(){
  return <Charts/>
}
