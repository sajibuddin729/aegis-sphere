import json
from pathlib import Path
import numpy as np

INDEX = Path("data/policies/china/index")


def test_dense_index_dimension():
    vectors = np.load(INDEX / "dense-vectors-v1.npy")
    assert vectors.shape[0] > 0
    assert vectors.shape[1] == 1024


def test_index_metadata_matches():
    vectors = np.load(INDEX / "dense-vectors-v1.npy")
    meta = json.loads((INDEX / "index-meta-v1.json").read_text())
    assert meta["dimension"] == 1024
    assert meta["chunk_count"] == vectors.shape[0]
