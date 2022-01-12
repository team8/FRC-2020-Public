import { Line, Chart } from "react-chartjs-2";
import React from "react";

const Chart2 = (props) => {
  const data = {
    title: props.title,
    labels: props.labels,
    datasets: props.data,
    width: props.width,
    height: props.height
  };

  const options = {
    maintainAspectRatio: false,
    responsive: true,
    elements: {
      point: {
        radius: 0
      },
      line: {
        borderWidth: 1.5
      }
    },
    plugins: {
      zoom: {
        zoom: {
          wheel: {
            enabled: true // SET SCROOL ZOOM TO TRUE
          },
          mode: "xy",
          speed: 100
        },
        pan: {
          enabled: true,
          mode: "xy",
          speed: 100
        }
      }
    }
  };

  return (
    <div>
      <Line
        type="line"
        data={data}
        options={options}
        width={900}
        height={450}
      />
    </div>
  );
};

export default Chart2;
