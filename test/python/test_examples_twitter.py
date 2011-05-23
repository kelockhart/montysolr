
import unittest
from montysolr_testcase import MontySolrTestCase, sj

import os
import time
import sys


class Test(MontySolrTestCase):

    def setUp(self):
        self.setSolrHome(os.path.join(self.getBaseDir(), 'examples/twitter/solr'))
        self.setDataDir(os.path.join(self.getBaseDir(), 'examples/twitter/solr/data'))
        self.setHandler(self.loadHandler('montysolr.examples.twitter_test'))
        MontySolrTestCase.setUp(self)


    def test_twitter(self):
        '''Index docs fetched by twitter api'''

        hm = sj.HashMap().of_(sj.String, sj.String)
        hm.put('action', 'search')
        hm.put('term', 'libya')
        params = sj.MapSolrParams(hm)

        req = sj.LocalSolrQueryRequest(self.core, params)
        rsp = sj.SolrQueryResponse()

        message = sj.PythonMessage('twitter_api') \
                    .setSender('TwitterAPIHandler') \
                    .setSolrQueryResponse(rsp) \
                    .setSolrQueryRequest(req)

        self.bridge.receive_message(message)

        total = sj.Integer.cast_(message.getResults())
        assert total



if __name__ == "__main__":
    #import sys;sys.argv = ['', 'Test.test_get_recids_changes4']
    unittest.main()
