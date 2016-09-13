# Process a directory of .tm2 files and output a csv containing
# information about each.

import csv
import os

OUT_FIELDS = ["file_id", "lat", "lon"]

def process_file(fn):
    """Read some of the header data from the tm2 file.
    See http://rredc.nrel.gov/solar/pubs/tmy2/tab3-1.html"""
    data = {}
    with open(fn, "r") as weather:
        weather.seek(1)
        data["file_id"] = "%s.tm2" % weather.read(5)
        data["lat"] = "%.2f" % get_lat(weather)
        data["lon"] = "%.2f" % get_lon(weather)

    print(data)
    return data

def get_lat(f):
    f.seek(37)
    direction = "%s" % f.read(1)
    f.seek(39)
    degrees = int(f.read(2))
    f.seek(42)
    minutes = int(f.read(2))
    return get_coord(direction, degrees, minutes)

def get_lon(f):
    f.seek(45)
    direction = "%s" % f.read(1)
    f.seek(47)
    degrees = int(f.read(3))
    f.seek(51)
    minutes = int(f.read(2))
    return get_coord(direction, degrees, minutes)

def get_coord(direction, degrees, minutes):
    coord = degrees + minutes / 60
    if direction == "W" or direction == "S":
        return coord * -1
    else:
        return coord

def main():
    with open("tmy2_data.csv", "w", newline="") as outfile:
        writer = csv.DictWriter(outfile, OUT_FIELDS)
        writer.writeheader()
        for fn in (name for name in os.listdir(".") if name.endswith(".tm2")):
            writer.writerow(process_file(fn))

if __name__ == "__main__":
    main()
