import pandas as pd
import matplotlib.pyplot as plt
import argparse

parser = argparse.ArgumentParser(description='CSV Graph Tool')
parser.add_argument('--data', type=str, nargs='*', default=['canlog.csv'], help='Input Data to Graph')

args = parser.parse_args()

for datum in args.data:
    print(datum)
    # data = pd.read_csv(datum, index_col=0, names=['name', 'time', 'value'], header=None)
    # print(data)








# import matplotlib.pyplot as plt
# import sys
# import csv
#
# #AUTHOR: Jason Liu
#
# keys = []
# values = []
# args = sys.argv
# args.pop(0)
#
# if len(args) == 0:
#     print('Please enter some arguments')
# else:
#     with open('canlog.csv', 'r') as csvfile:
#         plots = csv.reader(csvfile, delimiter = ',')
#         for row in plots:
#             if(not row[0] in keys):
#                 keys.append(row[0])
#                 values.append([])
#                 values[len(values) - 1].append([])
#                 values[len(values) - 1].append([])
#
#     with open('canlog.csv', 'r') as csvfile:
#         plots = csv.reader(csvfile, delimiter = ',')
#         for row in plots:
#             keyIndex = keys.index(row[0])
#             values[keyIndex][0].append(row[1])
#             values[keyIndex][1].append(row[2])
#
#     to_plot = []
#
#     for word in args:
#         if word in keys:
#             to_plot.append(keys.index(word))
#         else:
#             print(word + ' is not a valid key.')
#
#     fig, ax = plt.subplots()
#     ax.set_title('Data\nClick to toggle on/off')
#     ax.grid(color='k', linestyle='-', linewidth=0.4)
#     ax.set_xlabel('Time')
#     ax.set_ylabel('Value')
#
#     lines = []
#     for i in to_plot:
#         temp, = ax.plot(values[i][0], values[i][1], label = keys[i])
#         lines.append(temp)
#
#     leg = ax.legend(loc='upper left', fancybox=True, shadow=True)
#     leg.get_frame().set_alpha(1)
#
#     lined = dict()
#     for legline, origline in zip(leg.get_lines(), lines):
#         legline.set_picker(5)
#         lined[legline] = origline
#
#     def onpick(event):
#         legline = event.artist
#         origline = lined[legline]
#         vis = not origline.get_visible()
#         origline.set_visible(vis)
#
#         if vis:
#             legline.set_alpha(1.0)
#         else:
#             legline.set_alpha(0.2)
#         fig.canvas.draw()
#
#     fig.canvas.mpl_connect('pick_event', onpick)
#     plt.show()
