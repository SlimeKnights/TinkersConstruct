
import sys, os, csv

myDir = os.path.dirname(sys.argv[0])

srg = {}

for fn in ["fields.csv", "methods.csv"]:
  fn = os.path.join(myDir, fn)
  reader = csv.DictReader(open(fn))
  for entry in reader:
    srg[entry["searge"]] = entry["name"]


def handleFile(filename):
  if not filename.endswith(".java"):
    print filename, ": not a java source file?"
    return
  contents = open(filename).read()
  count = 0
  for k,v in srg.iteritems():
    if k not in contents: continue
    if count == 0:
      print filename, ": ",
    count += 1
    print k + "->" + v,
    contents = contents.replace(k, v)
  if count: print
  open(filename, 'w').write(contents)
  pass


for arg in sys.argv[1:]:
  if os.path.isdir(arg):
    for dirpath, dirnames, filenames in os.walk(arg):
      for fn in filenames:
        handleFile(os.path.join(dirpath, fn))
  else:
    handleFile(arg)

print "Use git add -e"
