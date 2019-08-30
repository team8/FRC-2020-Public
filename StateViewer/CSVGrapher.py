import pandas as pd
import argparse

import plotly.express as px

parser = argparse.ArgumentParser(description='CSV Graph Tool')
parser.add_argument('--data', type=str, nargs='+', help='Input Data to Graph')

args = parser.parse_args()

if args.data:
    data = pd.read_csv('canlog.csv', names=['name', 'time', 'value'], header=None)
    df = data[data.name.isin(args.data)]
    fig = px.line(df, x='time', y='value', color='name', title='Readings', height=900)
    fig.show()
