/* file: DistributedStep2MasterInputId.java */
/*******************************************************************************
* Copyright 2014-2017 Intel Corporation
* All Rights Reserved.
*
* If this  software was obtained  under the  Intel Simplified  Software License,
* the following terms apply:
*
* The source code,  information  and material  ("Material") contained  herein is
* owned by Intel Corporation or its  suppliers or licensors,  and  title to such
* Material remains with Intel  Corporation or its  suppliers or  licensors.  The
* Material  contains  proprietary  information  of  Intel or  its suppliers  and
* licensors.  The Material is protected by  worldwide copyright  laws and treaty
* provisions.  No part  of  the  Material   may  be  used,  copied,  reproduced,
* modified, published,  uploaded, posted, transmitted,  distributed or disclosed
* in any way without Intel's prior express written permission.  No license under
* any patent,  copyright or other  intellectual property rights  in the Material
* is granted to  or  conferred  upon  you,  either   expressly,  by implication,
* inducement,  estoppel  or  otherwise.  Any  license   under such  intellectual
* property rights must be express and approved by Intel in writing.
*
* Unless otherwise agreed by Intel in writing,  you may not remove or alter this
* notice or  any  other  notice   embedded  in  Materials  by  Intel  or Intel's
* suppliers or licensors in any way.
*
*
* If this  software  was obtained  under the  Apache License,  Version  2.0 (the
* "License"), the following terms apply:
*
* You may  not use this  file except  in compliance  with  the License.  You may
* obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*
*
* Unless  required  by   applicable  law  or  agreed  to  in  writing,  software
* distributed under the License  is distributed  on an  "AS IS"  BASIS,  WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*
* See the   License  for the   specific  language   governing   permissions  and
* limitations under the License.
*******************************************************************************/

/**
 * @ingroup svd_distributed
 * @{
 */
package com.intel.daal.algorithms.svd;

/**
 * <a name="DAAL-CLASS-ALGORITHMS__SVD__DISTRIBUTEDSTEP2MASTERINPUTID"></a>
 * @brief Partial results from previous steps of the SVD algorithm in the distributed processing mode, required by the second step
 */
public final class DistributedStep2MasterInputId {
    /** @private */
    static {
        System.loadLibrary("JavaAPI");
    }

    private int _value;

    /**
     * Constructs the master node input object identifier using the provided value
     * @param value     Value corresponding to the master node input object identifier
     */
    public DistributedStep2MasterInputId(int value) {
        _value = value;
    }

    /**
     * Returns the value corresponding to the master node input object identifier
     * @return Value corresponding to the master node input object identifier
     */
    public int getValue() {
        return _value;
    }

    private static final int inputOfStep2FromStep1Id = 0;

    public static final DistributedStep2MasterInputId inputOfStep2FromStep1 = new DistributedStep2MasterInputId(
            inputOfStep2FromStep1Id); /*!< DataCollection with data transferred from the first step to the second step in the distributed
                                         * processing mode */
}
/** @} */