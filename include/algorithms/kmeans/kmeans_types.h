/* file: kmeans_types.h */
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

/*
//++
//  Implementation of the K-Means algorithm interface.
//--
*/

#ifndef __KMEANS_TYPES_H__
#define __KMEANS_TYPES_H__

#include "algorithms/algorithm.h"
#include "data_management/data/numeric_table.h"
#include "data_management/data/homogen_numeric_table.h"
#include "services/daal_defines.h"

namespace daal
{
namespace algorithms
{
/**
 * @defgroup kmeans_compute Computation
 * \copydoc daal::algorithms::kmeans
 * @ingroup kmeans
 * @{
 */
/** \brief Contains classes of the K-Means algorithm */
namespace kmeans
{
/**
 * <a name="DAAL-ENUM-ALGORITHMS__KMEANS__METHOD"></a>
 * Available methods of the K-Means algorithm
 */
enum Method
{
    lloydDense = 0,     /*!< Default: performance-oriented method, synonym of defaultDense */
    defaultDense = 0,   /*!< Default: performance-oriented method, synonym of lloydDense */
    lloydCSR = 1        /*!< Implementation of the Lloyd algorithm for CSR numeric tables */
};

/**
 * <a name="DAAL-ENUM-ALGORITHMS__KMEANS__DISTANCETYPE"></a>
 * Supported distance types
 */
enum DistanceType
{
    euclidean, /*!< Euclidean distance */
    lastDistanceType = euclidean
};

/**
 * <a name="DAAL-ENUM-ALGORITHMS__KMEANS__INPUTID"></a>
 * \brief Available identifiers of input objects for the K-Means algorithm
 */
enum InputId
{
    data,            /*!< %Input data table */
    inputCentroids,  /*!< Initial centroids for the algorithm */
    lastInputId = inputCentroids
};

/**
 * <a name="DAAL-ENUM-ALGORITHMS__KMEANS__MASTERINPUTID"></a>
 * \brief Available identifiers of input objects for the K-Means algorithm in the distributed processing mode
 */
enum MasterInputId
{
    partialResults,   /*!< Collection of partial results computed on local nodes  */
    lastMasterInputId = partialResults
};

/**
 * <a name="DAAL-ENUM-ALGORITHMS__KMEANS__PARTIALRESULTID"></a>
 * \brief Available identifiers of partial results of the K-Means algorithm in the distributed processing mode
 */
enum PartialResultId
{
    nObservations,                                   /*!< Table containing the number of observations assigned to centroids */
    partialSums,                                     /*!< Table containing the sum of observations assigned to centroids */
    partialObjectiveFunction,                        /*!< Table containing an objective function value */
    partialGoalFunction = partialObjectiveFunction,  /*!< Table containing an objective function value \DAAL_DEPRECATED */
    partialAssignments,                              /*!< Table containing assignments of observations to particular clusters */
    lastPartialResultId = partialAssignments
};

/**
 * <a name="DAAL-ENUM-ALGORITHMS__KMEANS__RESULTID"></a>
 * \brief Available identifiers of results of the K-Means algorithm
 */
enum ResultId
{
    centroids,                        /*!< Table containing cluster centroids */
    assignments,                      /*!< Table containing assignments of observations to particular clusters */
    objectiveFunction,                /*!< Table containing an objective function value */
    goalFunction = objectiveFunction, /*!< Table containing an objective function value \DAAL_DEPRECATED */
    nIterations,                      /*!< Table containing the number of executed iterations */
    lastResultId = nIterations
};

/**
 * \brief Contains version 1.0 of the Intel(R) Data Analytics Acceleration Library (Intel(R) DAAL) interface.
 */
namespace interface1
{
/**
 * <a name="DAAL-STRUCT-ALGORITHMS__KMEANS__PARAMETER"></a>
 * \brief Parameters for the K-Means algorithm
 * \par Enumerations
 *      - \ref DistanceType Methods for distance computation
 *
 * \snippet kmeans/kmeans_types.h Parameter source code
 */
/* [Parameter source code] */
struct DAAL_EXPORT Parameter : public daal::algorithms::Parameter
{
    /**
     *  Constructs parameters of the K-Means algorithm
     *  \param[in] _nClusters   Number of clusters
     *  \param[in] _maxIterations Number of iterations
     */
    Parameter(size_t _nClusters, size_t _maxIterations);

    /**
     *  Constructs parameters of the K-Means algorithm by copying another parameters of the K-Means algorithm
     *  \param[in] other    Parameters of the K-Means algorithm
     */
    Parameter(const Parameter &other);

    size_t nClusters;                                      /*!< Number of clusters */
    size_t maxIterations;                                  /*!< Number of iterations */
    double accuracyThreshold;                              /*!< Threshold for the termination of the algorithm */
    double gamma;                                          /*!< Weight used in distance computation for categorical features */
    DistanceType distanceType;                             /*!< Distance used in the algorithm */
    bool assignFlag;                                       /*!< Do data points assignment */

    services::Status check() const DAAL_C11_OVERRIDE;
};
/* [Parameter source code] */

/**
 * <a name="DAAL-CLASS-ALGORITHMS__KMEANS__INPUTIFACE"></a>
 * \brief Interface for input objects for the the K-Means algorithm in the batch and distributed processing modes
 */
class DAAL_EXPORT InputIface : public daal::algorithms::Input
{
public:
    InputIface(size_t nElements) : daal::algorithms::Input(nElements) {};

    virtual size_t getNumberOfFeatures() const = 0;
};

/**
 * <a name="DAAL-CLASS-ALGORITHMS__KMEANS__INPUT"></a>
 * \brief %Input objects for the K-Means algorithm
 */
class DAAL_EXPORT Input : public InputIface
{
public:
    Input();
    virtual ~Input() {}

    /**
     * Returns an input object for the K-Means algorithm
     * \param[in] id    Identifier of the input object
     * \return          %Input object that corresponds to the given identifier
     */
    data_management::NumericTablePtr get(InputId id) const;

    /**
     * Sets an input object for the K-Means algorithm
     * \param[in] id    Identifier of the input object
     * \param[in] ptr   Pointer to the object
     */
    void set(InputId id, const data_management::NumericTablePtr &ptr);


    /**
     * Returns the number of features in the input object
     * \return Number of features in the input object
     */
    size_t getNumberOfFeatures() const DAAL_C11_OVERRIDE;

    /**
     * Checks input objects for the K-Means algorithm
     * \param[in] par     Algorithm parameter
     * \param[in] method  Computation method of the algorithm
     */
    services::Status check(const daal::algorithms::Parameter *par, int method) const DAAL_C11_OVERRIDE;
};

/**
 * <a name="DAAL-CLASS-ALGORITHMS__KMEANS__PARTIALRESULT"></a>
 * \brief Partial results obtained with the compute() method of the K-Means algorithm in the batch processing mode
 */
class DAAL_EXPORT PartialResult : public daal::algorithms::PartialResult
{
public:
    DECLARE_SERIALIZABLE_CAST(PartialResult);
    PartialResult();

    virtual ~PartialResult() {};

    /**
     * Allocates memory to store partial results of the K-Means algorithm
     * \param[in] input        Pointer to the structure of the input objects
     * \param[in] parameter    Pointer to the structure of the algorithm parameters
     * \param[in] method       Computation method of the algorithm
     */
    template <typename algorithmFPType>
    DAAL_EXPORT services::Status allocate(const daal::algorithms::Input *input, const daal::algorithms::Parameter *parameter, const int method);

    /**
     * Returns a partial result of the K-Means algorithm
     * \param[in] id   Identifier of the partial result
     * \return         Partial result that corresponds to the given identifier
     */
    data_management::NumericTablePtr get(PartialResultId id) const;

    /**
     * Sets a partial result of the K-Means algorithm
     * \param[in] id    Identifier of the partial result
     * \param[in] ptr   Pointer to the object
     */
    void set(PartialResultId id, const data_management::NumericTablePtr &ptr);

    /**
     * Returns the number of features in the Input data table
     * \return Number of features in the Input data table
     */

    size_t getNumberOfFeatures() const;

    /**
     * Checks partial results of the K-Means algorithm
     * \param[in] input   %Input object of the algorithm
     * \param[in] par     Algorithm parameter
     * \param[in] method  Computation method
     */
    services::Status check(const daal::algorithms::Input *input, const daal::algorithms::Parameter *par, int method) const DAAL_C11_OVERRIDE;

    /**
     * Checks partial results of the K-Means algorithm
     * \param[in] par     Algorithm parameter
     * \param[in] method  Computation method
     */
    services::Status check(const daal::algorithms::Parameter *par, int method) const DAAL_C11_OVERRIDE;

protected:
    /** \private */
    template<typename Archive, bool onDeserialize>
    services::Status serialImpl(Archive *arch)
    {
        daal::algorithms::PartialResult::serialImpl<Archive, onDeserialize>(arch);

        return services::Status();
    }

    services::Status serializeImpl(data_management::InputDataArchive  *arch) DAAL_C11_OVERRIDE
    {
        serialImpl<data_management::InputDataArchive, false>(arch);

        return services::Status();
    }

    services::Status deserializeImpl(const data_management::OutputDataArchive *arch) DAAL_C11_OVERRIDE
    {
        serialImpl<const data_management::OutputDataArchive, true>(arch);

        return services::Status();
    }
};
typedef services::SharedPtr<PartialResult> PartialResultPtr;

/**
 * <a name="DAAL-CLASS-ALGORITHMS__KMEANS__RESULT"></a>
 * \brief Results obtained with the compute() method of the K-Means algorithm in the batch processing mode
 */
class DAAL_EXPORT Result : public daal::algorithms::Result
{
public:
    DECLARE_SERIALIZABLE_CAST(Result);
    Result();

    virtual ~Result() {};

    /**
     * Allocates memory to store the results of the K-Means algorithm
     * \param[in] input     Pointer to the structure of the input objects
     * \param[in] parameter Pointer to the structure of the algorithm parameters
     * \param[in] method    Computation method
     */
    template <typename algorithmFPType>
    DAAL_EXPORT services::Status allocate(const daal::algorithms::Input *input, const daal::algorithms::Parameter *parameter, const int method);

    /**
     * Allocates memory to store the results of the K-Means algorithm
     * \param[in] partialResult Pointer to the partial result structure
     * \param[in] parameter     Pointer to the structure of the algorithm parameters
     * \param[in] method        Computation method
     */
    template <typename algorithmFPType>
    DAAL_EXPORT services::Status allocate(const daal::algorithms::PartialResult *partialResult, const daal::algorithms::Parameter *parameter, const int method);

    /**
     * Returns the result of the K-Means algorithm
     * \param[in] id   Result identifier
     * \return         Result that corresponds to the given identifier
     */
    data_management::NumericTablePtr get(ResultId id) const;

    /**
     * Sets the result of the K-Means algorithm
     * \param[in] id    Identifier of the result
     * \param[in] ptr   Pointer to the object
     */
    void set(ResultId id, const data_management::NumericTablePtr &ptr);

    /**
     * Checks the result of the K-Means algorithm
     * \param[in] input   %Input objects for the algorithm
     * \param[in] par     Algorithm parameter
     * \param[in] method  Computation method
     */
    services::Status check(const daal::algorithms::Input *input, const daal::algorithms::Parameter *par, int method) const DAAL_C11_OVERRIDE;

    /**
     * Checks the results of the K-Means algorithm
     * \param[in] pres    Partial results of the algorithm
     * \param[in] par     Algorithm parameter
     * \param[in] method  Computation method
     */
    services::Status check(const daal::algorithms::PartialResult *pres, const daal::algorithms::Parameter *par, int method) const DAAL_C11_OVERRIDE;

protected:
    /** \private */
    template<typename Archive, bool onDeserialize>
    services::Status serialImpl(Archive *arch)
    {
        daal::algorithms::Result::serialImpl<Archive, onDeserialize>(arch);

        return services::Status();
    }

    services::Status serializeImpl(data_management::InputDataArchive  *arch) DAAL_C11_OVERRIDE
    {
        serialImpl<data_management::InputDataArchive, false>(arch);

        return services::Status();
    }

    services::Status deserializeImpl(const data_management::OutputDataArchive *arch) DAAL_C11_OVERRIDE
    {
        serialImpl<const data_management::OutputDataArchive, true>(arch);

        return services::Status();
    }
};
typedef services::SharedPtr<Result> ResultPtr;

/**
 * <a name="DAAL-CLASS-ALGORITHMS__KMEANS__DISTRIBUTEDSTEP2MASTERINPUT"></a>
 * \brief %Input objects for the K-Means algorithm in the distributed processing mode
 */
class DAAL_EXPORT DistributedStep2MasterInput : public InputIface
{
public:
    DistributedStep2MasterInput();

    virtual ~DistributedStep2MasterInput() {}

    /**
     * Returns an input object for the K-Means algorithm in the second step of the distributed processing mode
     * \param[in] id    Identifier of the input object
     * \return          %Input object that corresponds to the given identifier
     */
    data_management::DataCollectionPtr get(MasterInputId id) const;

    /**
     * Sets an input object for the K-Means algorithm in the second step of the distributed processing mode
     * \param[in] id    Identifier of the input object
     * \param[in] ptr   Pointer to the object
     */
    void set(MasterInputId id, const data_management::DataCollectionPtr &ptr);

    /**
     * Adds partial results computed on local nodes to the input for the K-Means algorithm
     * in the second step of the distributed processing mode
     * \param[in] id    Identifier of the input object
     * \param[in] value Pointer to the object
     */
    void add(MasterInputId id, const PartialResultPtr &value);

    /**
     * Returns the number of features in the Input data table in the second step of the distributed processing mode
     * \return Number of features in the Input data table
     */

    size_t getNumberOfFeatures() const DAAL_C11_OVERRIDE;

    /**
     * Checks an input object for the K-Means algorithm in the second step of the distributed processing mode
     * \param[in] par     Algorithm parameter
     * \param[in] method  Computation method
     */
    services::Status check(const daal::algorithms::Parameter *par, int method) const DAAL_C11_OVERRIDE;
};
} // namespace interface1
using interface1::Parameter;
using interface1::InputIface;
using interface1::Input;
using interface1::PartialResult;
using interface1::PartialResultPtr;
using interface1::Result;
using interface1::ResultPtr;
using interface1::DistributedStep2MasterInput;

} // namespace daal::algorithms::kmeans
/** @} */
} // namespace daal::algorithms
} // namespace daal
#endif